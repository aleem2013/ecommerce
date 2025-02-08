package com.ecommerce.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
   
   private Long productId;

   public ProductNotFoundException(Long productId) {
       super("Product not found with id: " + productId);
       this.productId = productId;
   }

   public ProductNotFoundException(String message) {
       super(message);
   }

   public ProductNotFoundException(String message, Throwable cause) {
       super(message, cause);
   }

   public Long getProductId() {
       return productId;
   }
}
