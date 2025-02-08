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
public class ProductEvent {
   private Long productId;
   private String eventType;
   private LocalDateTime timestamp;
   private Product product;
   private Map<String, Object> metadata;

   public ProductEvent(Long productId, String eventType, Product product) {
       this.productId = productId;
       this.eventType = eventType;
       this.product = product;
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
   public boolean isStockUpdate() {
       return "STOCK_UPDATED".equals(eventType);
   }

   @JsonIgnore
   public boolean isLowStockAlert() {
       return "LOW_STOCK_ALERT".equals(eventType);
   }

   @JsonIgnore
   public boolean isProductCreated() {
       return "PRODUCT_CREATED".equals(eventType);
   }

   @JsonIgnore
   public boolean isProductUpdated() {
       return "PRODUCT_UPDATED".equals(eventType);
   }

   @JsonIgnore
   public boolean isProductDeleted() {
       return "PRODUCT_DELETED".equals(eventType);
   }
}
