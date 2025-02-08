package com.ecommerce.demo.component;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ecommerce.demo.model.NotificationMessage;
import com.ecommerce.demo.service.EmailService;

import lombok.RequiredArgsConstructor;

// RabbitMQ Consumer
@Component
@RequiredArgsConstructor
public class NotificationConsumer {
   private final EmailService emailService;

   @RabbitListener(queues = "notification-queue")
   public void handleNotification(NotificationMessage message) {
       emailService.sendEmail(message.getTo(), message.getMessage());
   }
}