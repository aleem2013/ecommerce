package com.ecommerce.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @NotBlank(message = "Product name is required")
   private String name;

   @NotNull(message = "Price is required")
   @Positive(message = "Price must be positive")
   @Column(precision = 10, scale = 2)
   private BigDecimal price;

   @NotNull(message = "Stock is required")
   @PositiveOrZero(message = "Stock cannot be negative")
   private Integer stock;

   private String category;

   @Convert(converter = JsonAttributeConverter.class)
   @Column(columnDefinition = "jsonb")
   private Map<String, Object> attributes;

   @Version
   private Long version;

   @CreatedDate
   private LocalDateTime createdAt;

   @LastModifiedDate
   private LocalDateTime updatedAt;

   @PrePersist
   protected void onCreate() {
       createdAt = LocalDateTime.now();
       updatedAt = LocalDateTime.now();
   }

   @PreUpdate
   protected void onUpdate() {
       updatedAt = LocalDateTime.now();
   }
}