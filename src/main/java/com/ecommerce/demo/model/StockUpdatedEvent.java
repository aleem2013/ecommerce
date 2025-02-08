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
public class StockUpdatedEvent extends DomainEvent {
    private Long productId;
    private Integer quantityChange;
    private Integer newStockLevel;

    public StockUpdatedEvent(Long productId, Integer quantityChange, Integer newStockLevel) {
        super(productId.toString(), "STOCK_UPDATED");
        this.productId = productId;
        this.quantityChange = quantityChange;
        this.newStockLevel = newStockLevel;

        // Store the stock update details in event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("productId", productId);
        eventData.put("quantityChange", quantityChange);
        eventData.put("newStockLevel", newStockLevel);
        eventData.put("timestamp", this.getTimestamp());
        
        this.setData(eventData);
    }

    // Helper method to reconstruct stock level from events
    public static Integer reconstructStockLevel(List<DomainEvent> events) {
        if (events.isEmpty()) {
            return null;
        }

        // Get the latest stock update event
        StockUpdatedEvent latestEvent = (StockUpdatedEvent) events.get(events.size() - 1);
        return latestEvent.getNewStockLevel();
    }
}
