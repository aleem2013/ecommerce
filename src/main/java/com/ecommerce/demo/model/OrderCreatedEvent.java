package com.ecommerce.demo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends DomainEvent {
    private Order order;

    public OrderCreatedEvent(Order order) {
        super(order.getId().toString(), "ORDER_CREATED");
        this.order = order;
        
        // Convert order details to event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("customerEmail", order.getCustomerEmail());
        eventData.put("status", order.getStatus().name());
        eventData.put("items", order.getItems());
        eventData.put("shippingAddress", order.getShippingAddress());
        
        this.setData(eventData);
    }

    // Reconstruct order from event data
    public static Order reconstruct(List<DomainEvent> events) {
        if (events.isEmpty()) {
            return null;
        }

        OrderCreatedEvent createEvent = (OrderCreatedEvent) events.get(0);
        Map<String, Object> data = createEvent.getData();
        
        return Order.builder()
            .id(Long.valueOf(createEvent.getAggregateId()))
            .customerEmail((String) data.get("customerEmail"))
            .status(OrderStatus.valueOf((String) data.get("status")))
            .items((List<OrderItem>) data.get("items"))
            .shippingAddress((Address) data.get("shippingAddress"))
            .build();
    }
}
