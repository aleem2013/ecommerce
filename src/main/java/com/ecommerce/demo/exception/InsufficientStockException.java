package com.ecommerce.demo.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId, Integer available, Integer requested) {
        super("Insufficient stock for product " + productId + 
              ". Available: " + available + ", Requested: " + requested);
    }
}
