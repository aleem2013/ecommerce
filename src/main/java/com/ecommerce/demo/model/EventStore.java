package com.ecommerce.demo.model;

import java.time.LocalDateTime;
import java.util.List;

public interface EventStore {
    void save(DomainEvent event);
    List<DomainEvent> getEvents(String aggregateId);
    
    // Added methods for more functionality
    List<DomainEvent> getEventsByType(String type);
    List<DomainEvent> getEventsByTimeRange(LocalDateTime start, LocalDateTime end);
    void saveAll(List<DomainEvent> events);
}
