package com.ecommerce.demo.exception;

public class InvalidProductException extends RuntimeException {
   
    public InvalidProductException(String message) {
        super(message);
    }
 
    public InvalidProductException(String message, Throwable cause) {
        super(message, cause);
    }
 }
