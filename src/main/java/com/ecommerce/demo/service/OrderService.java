package com.ecommerce.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ecommerce.demo.error.ErrorResponse;
import com.ecommerce.demo.exception.InsufficientStockException;
import com.ecommerce.demo.exception.OrderNotFoundException;
import com.ecommerce.demo.exception.ProductNotFoundException;
import com.ecommerce.demo.model.NotificationMessage;
import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderEvent;
import com.ecommerce.demo.model.OrderItem;
import com.ecommerce.demo.model.OrderRequest;
import com.ecommerce.demo.model.OrderStatus;
import com.ecommerce.demo.model.Product;
import com.ecommerce.demo.model.OrderRequest.OrderItemRequest;
import com.ecommerce.demo.repository.OrderRepository;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class OrderService {

   private final OrderRepository orderRepository;
   private final ProductService productService;
   private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
   private final RabbitTemplate rabbitTemplate;
   private final RedisTemplate<String, Order> redisTemplate;
   private final CircuitBreaker circuitBreaker;

   public OrderService(OrderRepository orderRepository,
                      ProductService productService,
                      KafkaTemplate<String, OrderEvent> kafkaTemplate,
                      RabbitTemplate rabbitTemplate,
                      RedisTemplate<String, Order> redisTemplate,
                      CircuitBreakerRegistry circuitBreakerRegistry) {
       this.orderRepository = orderRepository;
       this.productService = productService;
       this.kafkaTemplate = kafkaTemplate;
       this.rabbitTemplate = rabbitTemplate;
       this.redisTemplate = redisTemplate;
       this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("orderService");
   }

   @Transactional
   public Order createOrder(OrderRequest request) {
       // Validate stock availability
       validateStock(request.getItems());

       // Create order
       Order order = request.toOrder();
       order = orderRepository.save(order);

       // Update stock
       updateProductStock(order.getItems());

       // Send events
       sendOrderCreatedEvent(order);
       sendOrderNotification(order);

       cacheOrder(order); // Cache the new order

       return order;
   }

   @Cacheable(value = "orders", key = "#id")
   public Order getOrder(Long id) {
       return orderRepository.findById(id)
               .orElseThrow(() -> new OrderNotFoundException(id));
   }

    @CachePut(value = "orders", key = "#id")
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrder(id);
        order.setStatus(status);
        order = orderRepository.save(order);
        sendOrderStatusUpdateEvent(order);
        return order;
    }

   public Page<Order> getOrdersByCustomer(String email, Pageable pageable) {
       return orderRepository.findByCustomerEmail(email, pageable);
   }

   private void cacheOrder(Order order) {
        try {
            String key = "orders::" + order.getId();
            redisTemplate.opsForValue().set(key, order);
        } catch (Exception e) {
            log.error("Error caching order", e);
        }
   }

   private void validateStock(List<OrderItemRequest> items) {
       for (OrderItemRequest item : items) {
           Product product = productService.getProduct(item.getProductId())
                   .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
           
           if (product.getStock() < item.getQuantity()) {
               throw new InsufficientStockException(product.getId(), product.getStock(), item.getQuantity());
           }
       }
   }

   @Transactional
   private void updateProductStock(List<OrderItem> items) {
       items.forEach(item -> 
           productService.updateStock(item.getProductId(), -item.getQuantity())
       );
   }

   private void sendOrderCreatedEvent(Order order) {
    try {
        OrderEvent event = new OrderEvent(order.getId(), "ORDER_CREATED", order);
        kafkaTemplate.send("order-events", event)
            .thenApply(result -> {
                log.info("Order event sent successfully");
                return result;
            })
            .exceptionally(ex -> {
                log.error("Failed to send order event", ex);
                return null;
            });
    } catch (Exception e) {
        log.error("Error sending order created event", e);
    }
}

   private void sendOrderNotification(Order order) {
       NotificationMessage notification = NotificationMessage.builder()
               .to(order.getCustomerEmail())
               .subject("Order Confirmation")
               .message("Your order " + order.getId() + " has been created")
               .build();

       circuitBreaker.executeSupplier(() -> {
           rabbitTemplate.convertAndSend("notification-exchange", 
                                       "order.notification", 
                                       notification);
           return true;
       });
   }

   private void sendOrderStatusUpdateEvent(Order order) {
       OrderEvent event = new OrderEvent(order.getId(), 
                                       "ORDER_STATUS_UPDATED", 
                                       order);
       kafkaTemplate.send("order-events", event);
   }

   @Scheduled(fixedRate = 300000) // Every 5 minutes
public void processFailedOrders() {
    List<Order> failedOrders = orderRepository
            .findByStatusAndCreatedAtBefore(
                OrderStatus.CREATED, 
                LocalDateTime.now().minusMinutes(15)
            );
    
    failedOrders.forEach(order -> {
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        // Restore stock
        List<OrderItem> reverseItems = order.getItems().stream()
                .map(item -> OrderItem.builder()
                    .productId(item.getProductId())
                    .quantity(-item.getQuantity()) // Negative quantity to restore stock
                    .build())
                .collect(Collectors.toList());
        
        updateProductStock(reverseItems);
    });
}

   @ExceptionHandler(OrderNotFoundException.class)
   public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
       return ResponseEntity
               .status(HttpStatus.NOT_FOUND)
               .body(new ErrorResponse(ex.getMessage()));
   }
}
