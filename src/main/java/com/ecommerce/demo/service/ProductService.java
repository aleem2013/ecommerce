package com.ecommerce.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ecommerce.demo.constants.KafkaConstants;
import com.ecommerce.demo.exception.InsufficientStockException;
import com.ecommerce.demo.exception.InvalidProductException;
import com.ecommerce.demo.exception.ProductNotFoundException;
import com.ecommerce.demo.model.Product;
import com.ecommerce.demo.model.ProductEvent;
import com.ecommerce.demo.repository.ProductRepository;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductService {

   private final ProductRepository productRepository;
   private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
   private final RedisTemplate<String, Product> redisTemplate;
   private final CircuitBreaker circuitBreaker;

   public ProductService(ProductRepository productRepository,
                        KafkaTemplate<String, ProductEvent> kafkaTemplate,
                        RedisTemplate<String, Product> redisTemplate,
                        CircuitBreakerRegistry circuitBreakerRegistry) {
       this.productRepository = productRepository;
       this.kafkaTemplate = kafkaTemplate;
       this.redisTemplate = redisTemplate;
       this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("productService");
   }

   @Transactional
   public Product createProduct(Product product) {
       validateProduct(product);
       product = productRepository.save(product);
       sendProductEvent("PRODUCT_CREATED", product);
       cacheProduct(product);
       return product;
   }

   @Cacheable(value = "products", key = "#id")
   public Optional<Product> getProduct(Long id) {
       return circuitBreaker.executeSupplier(() -> 
           productRepository.findById(id)
       );
   }

   public Page<Product> getAllProducts(Pageable pageable) {
       return productRepository.findAll(pageable);
   }

   @CachePut(value = "products", key = "#id")
   @Transactional
   public Product updateProduct(Long id, Product updatedProduct) {
       Product existingProduct = getProduct(id)
           .orElseThrow(() -> new ProductNotFoundException(id));

       existingProduct.setName(updatedProduct.getName());
       existingProduct.setPrice(updatedProduct.getPrice());
       existingProduct.setStock(updatedProduct.getStock());
       existingProduct.setCategory(updatedProduct.getCategory());
       existingProduct.setAttributes(updatedProduct.getAttributes());

       existingProduct = productRepository.save(existingProduct);
       sendProductEvent("PRODUCT_UPDATED", existingProduct);
       return existingProduct;
   }

   @Transactional
   @CacheEvict(value = "products", key = "#id")
   public void deleteProduct(Long id) {
       Product product = getProduct(id)
           .orElseThrow(() -> new ProductNotFoundException(id));
           
       productRepository.delete(product);
       sendProductEvent("PRODUCT_DELETED", product);
   }

   @Transactional
   public Product updateStock(Long id, Integer quantityChange) {
       return productRepository.findById(id)
           .map(product -> {
               int newStock = product.getStock() + quantityChange;
               if (newStock < 0) {
                   throw new InsufficientStockException(id, product.getStock(), Math.abs(quantityChange));
               }
               product.setStock(newStock);
               Product updated = productRepository.save(product);
               sendProductEvent("STOCK_UPDATED", updated);
               cacheProduct(updated);
               return updated;
           })
           .orElseThrow(() -> new ProductNotFoundException(id));
   }

   private void validateProduct(Product product) {
       if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
           throw new InvalidProductException("Price must be greater than zero");
       }
       if (product.getStock() < 0) {
           throw new InvalidProductException("Stock cannot be negative");
       }
   }

   private void sendProductEvent(String eventType, Product product) {
    try {
        ProductEvent event = new ProductEvent(product.getId(), eventType, product);
        kafkaTemplate.send(KafkaConstants.PRODUCT_EVENTS_TOPIC, event)
            .thenApply(result -> {
                log.info("Product event sent successfully");
                return result;
            })
            .exceptionally(ex -> {
                log.error("Failed to send product event", ex);
                return null;
            });
    } catch (Exception e) {
        log.error("Error sending product event", e);
    }
}

   private void cacheProduct(Product product) {
       try {
           String key = "products::" + product.getId();
           redisTemplate.opsForValue().set(key, product);
       } catch (Exception e) {
           log.error("Error caching product", e);
       }
   }

   @Scheduled(fixedRate = 3600000) // Every hour
   public void checkLowStock() {
       List<Product> lowStockProducts = productRepository.findByStockLessThan(10);
       lowStockProducts.forEach(product -> 
           sendProductEvent("LOW_STOCK_ALERT", product)
       );
   }
}
