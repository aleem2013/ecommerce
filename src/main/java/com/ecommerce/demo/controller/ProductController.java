package com.ecommerce.demo.controller;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.demo.error.ErrorResponse;
import com.ecommerce.demo.exception.ProductNotFoundException;
import com.ecommerce.demo.model.Product;
import com.ecommerce.demo.model.ProductResponse;
import com.ecommerce.demo.model.ProductUpdateRequest;
import com.ecommerce.demo.model.StockUpdateRequest;
import com.ecommerce.demo.service.ProductService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

   private final ProductService productService;

   public ProductController(ProductService productService) {
       this.productService = productService;
   }

   @PostMapping
   @PreAuthorize("hasRole('ROLE_ADMIN')")
   public ResponseEntity<ProductResponse> createProduct(@Validated @RequestBody ProductUpdateRequest request) {
       log.info("Creating product: {}", request);
       Product product = productService.createProduct(request.toProduct());
       return ResponseEntity.status(HttpStatus.CREATED)
                          .body(ProductResponse.fromProduct(product));
   }

   @GetMapping("/{id}")
   @Cacheable(value = "products", key = "#id")
   public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
       Product product = productService.getProduct(id)
           .orElseThrow(() -> new ProductNotFoundException(id));
       return ResponseEntity.ok(ProductResponse.fromProduct(product));
   }

   @GetMapping
   public ResponseEntity<Page<ProductResponse>> getAllProducts(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "10") int size,
           @RequestParam(defaultValue = "id") String sortBy) {
       
       Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
       Page<Product> products = productService.getAllProducts(pageable);
       
       Page<ProductResponse> responses = products.map(ProductResponse::fromProduct);
       return ResponseEntity.ok(responses);
   }

   @PutMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   @CachePut(value = "products", key = "#id")
   public ResponseEntity<ProductResponse> updateProduct(
           @PathVariable Long id,
           @Validated @RequestBody ProductUpdateRequest request) {
       
       Product updated = productService.updateProduct(id, request.toProduct());
       return ResponseEntity.ok(ProductResponse.fromProduct(updated));
   }

   @DeleteMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   @CacheEvict(value = "products", key = "#id")
   public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
       productService.deleteProduct(id);
       return ResponseEntity.noContent().build();
   }

   @PutMapping("/{id}/stock")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<ProductResponse> updateStock(
           @PathVariable Long id,
           @Valid @RequestBody StockUpdateRequest request) {
       
       Product updated = productService.updateStock(id, request.getQuantity());
       return ResponseEntity.ok(ProductResponse.fromProduct(updated));
   }

   @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}