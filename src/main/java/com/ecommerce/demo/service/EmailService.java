package com.ecommerce.demo.service;

import org.springframework.stereotype.Service;

import com.ecommerce.demo.model.Order;

@Service
public interface EmailService {
    void sendEmail(String to, String subject, String message);
    void sendOrderConfirmationEmail(String to, Order order);
}
