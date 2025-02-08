package com.ecommerce.demo.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.demo.model.OrderEvent;
import com.ecommerce.demo.service.ProductService;

import lombok.RequiredArgsConstructor;

// Kafka Consumer
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
   private final ProductService productService;

   @KafkaListener(topics = "order-events", groupId = "ecommerce-group")
   public void handleOrderEvent(OrderEvent event) {
       if (event.isOrderCreated()) {
           event.getOrder().getItems().forEach(item -> 
               productService.updateStock(item.getProductId(), -item.getQuantity())
           );
       }
   }
}