package com.ecommerce.demo.component;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ecommerce.demo.constants.NotificationType;
import com.ecommerce.demo.model.NotificationMessage;
import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// RabbitMQ Consumer
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
   private final EmailService emailService;
   private final ObjectMapper objectMapper;

   @RabbitListener(queues = "notification-queue")
   public void handleNotification(NotificationMessage message) {
        try {
            if (message.getType() == NotificationType.ORDER_CONFIRMATION) {
                // Convert the LinkedHashMap to Order
                Order order = objectMapper.convertValue(message.getMetadata().get("order"), Order.class);
                emailService.sendOrderConfirmationEmail(message.getTo(), order);

                // Order order = (Order) message.getMetadata().get("order");
                // emailService.sendOrderConfirmationEmail(message.getTo(), order);
            } else if (message.getType() == NotificationType.EMAIL) {
                emailService.sendEmail(message.getTo(), message.getSubject(), message.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to process notification for: {}", message.getTo(), e);
            // You might want to implement retry logic or dead letter queue here
        }
    }
}