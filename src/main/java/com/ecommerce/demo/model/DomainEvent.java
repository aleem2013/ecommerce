package com.ecommerce.demo.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class DomainEvent {
   private String aggregateId;
   private String type;
   private Map<String, Object> data;
   private LocalDateTime timestamp;

   public DomainEvent(String aggregateId, String type) {
       this.aggregateId = aggregateId;
       this.type = type;
       this.data = new HashMap<>();
       this.timestamp = LocalDateTime.now();
   }

   public DomainEvent(String aggregateId, String type, Map<String, Object> data, LocalDateTime timestamp) {
        this.aggregateId = aggregateId;
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }

   public void addData(String key, Object value) {
       if (this.data == null) {
           this.data = new HashMap<>();
       }
       this.data.put(key, value);
   }
}
