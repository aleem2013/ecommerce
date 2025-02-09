package com.ecommerce.demo.component;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ecommerce.demo.model.NotificationMessage;
import com.ecommerce.demo.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// RabbitMQ Consumer
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
   private final EmailService emailService;

   @RabbitListener(queues = "notification-queue")
   public void handleNotification(NotificationMessage message) {
        try {
        emailService.sendEmail(message.getTo(), message.getMessage());
    } catch (Exception e) {
        log.error("Failed to process notification for: {}", message.getTo(), e);
        // You might want to implement retry logic or dead letter queue here
        }
    }
}