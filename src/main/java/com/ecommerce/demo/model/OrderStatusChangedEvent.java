package com.ecommerce.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // Include superclass fields in equals and hashCode
public class OrderStatusChangedEvent extends DomainEvent {
   private OrderStatus oldStatus;
   private OrderStatus newStatus;

   public OrderStatusChangedEvent(String orderId, OrderStatus oldStatus, OrderStatus newStatus) {
       super(orderId, "ORDER_STATUS_CHANGED");
       this.oldStatus = oldStatus;
       this.newStatus = newStatus;
   }
}
