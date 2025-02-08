package com.ecommerce.demo.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
   private Long id;
   private String name;
   private BigDecimal price;
   private Integer stock;
   private String category;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
   private Map<String, Object> attributes;

   public static ProductResponse fromProduct(Product product) {
       return ProductResponse.builder()
               .id(product.getId())
               .name(product.getName())
               .price(product.getPrice())
               .stock(product.getStock())
               .category(product.getCategory())
               .createdAt(product.getCreatedAt())
               .updatedAt(product.getUpdatedAt())
               .attributes(product.getAttributes())
               .build();
   }
}
