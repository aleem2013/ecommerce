package com.ecommerce.demo.model;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConcreteDomainEvent extends DomainEvent {
    public ConcreteDomainEvent(String aggregateId, String type, Map<String, Object> data, LocalDateTime timestamp) {
        super(aggregateId, type, data, timestamp);
    }
}
