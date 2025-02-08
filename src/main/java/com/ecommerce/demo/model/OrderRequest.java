package com.ecommerce.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
   @NotNull(message = "Customer email is required")
   @Email(message = "Invalid email format")
   private String customerEmail;

   @NotEmpty(message = "Order items cannot be empty")
   private List<@Valid OrderItemRequest> items;

   @Valid
   @NotNull(message = "Shipping address is required")
   private Address shippingAddress;

   @Valid
   private PaymentRequest payment;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public static class OrderItemRequest {
       @NotNull(message = "Product ID is required")
       private Long productId;

       @NotNull(message = "Quantity is required")
       @Positive(message = "Quantity must be positive")
       private Integer quantity;

       @NotNull(message = "Price is required")
       @Positive(message = "Price must be positive")
       private BigDecimal price;
   }   

   public Order toOrder() {
        List<OrderItem> orderItems = items.stream()
            .map(item -> OrderItem.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build())
            .toList();

        return Order.builder()
            .customerEmail(customerEmail)
            .items(orderItems)
            .shippingAddress(shippingAddress)
            .status(OrderStatus.CREATED)
            .build();
    }
}
