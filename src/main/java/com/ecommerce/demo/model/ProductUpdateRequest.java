package com.ecommerce.demo.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {
   @NotBlank(message = "Product name is required")
   private String name;

   @NotNull(message = "Price is required")
   @Positive(message = "Price must be positive")
   private BigDecimal price;

   @NotNull(message = "Stock is required")
   @PositiveOrZero(message = "Stock cannot be negative")
   private Integer stock;

   @NotBlank(message = "Category is required")
   private String category;
   
   private Map<String, Object> attributes;

   public Product toProduct() {
       return Product.builder()
               .name(this.name)
               .price(this.price)
               .stock(this.stock)
               .category(this.category)
               .attributes(this.attributes)
               .build();
   }
}
