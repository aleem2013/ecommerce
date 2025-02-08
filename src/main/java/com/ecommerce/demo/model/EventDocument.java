package com.ecommerce.demo.model;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDocument {
   @Id
   private String id;
   private String aggregateId;
   private String type;
   private Map<String, Object> data;
   private LocalDateTime timestamp;

   public EventDocument(String aggregateId, String type, Map<String, Object> data, LocalDateTime timestamp) {
        this.aggregateId = aggregateId;
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }
}
