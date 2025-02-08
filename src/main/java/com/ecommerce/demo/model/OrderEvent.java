package com.ecommerce.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
   private Long orderId;
   private String eventType;
   private LocalDateTime timestamp;
   private Order order;
   private Map<String, Object> metadata;

   public OrderEvent(Long orderId, String eventType, Order order) {
       this.orderId = orderId;
       this.eventType = eventType;
       this.order = order;
       this.timestamp = LocalDateTime.now();
       this.metadata = new HashMap<>();
   }

   public void addMetadata(String key, Object value) {
       if (metadata == null) {
           metadata = new HashMap<>();
       }
       metadata.put(key, value);
   }

   @JsonIgnore
   public boolean isOrderCreated() {
       return "ORDER_CREATED".equals(eventType);
   }

   @JsonIgnore
   public boolean isStatusUpdate() {
       return "ORDER_STATUS_UPDATED".equals(eventType);
   }
}
