package com.ecommerce.demo.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(String to, String message) {
        log.info("Sending email to: {}, message: {}", to, message);
        // Implement email sending logic
    }
}
