package com.ecommerce.demo.component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.ecommerce.demo.model.ConcreteDomainEvent;
import com.ecommerce.demo.model.DomainEvent;
import com.ecommerce.demo.model.EventDocument;
import com.ecommerce.demo.model.EventStore;

@Component
@RequiredArgsConstructor
public class MongoEventStore implements EventStore {
    private final MongoTemplate mongoTemplate;

    @Override
    public void save(DomainEvent event) {
        EventDocument document = new EventDocument(
            event.getAggregateId(),
            event.getType(),
            event.getData(),
            LocalDateTime.now()
        );
        mongoTemplate.save(document, "events");
    }

    @Override
    public void saveAll(List<DomainEvent> events) {
        List<EventDocument> documents = events.stream()
            .map(event -> new EventDocument(
                event.getAggregateId(),
                event.getType(),
                event.getData(),
                event.getTimestamp()))
            .collect(Collectors.toList());
        
        mongoTemplate.insertAll(documents);
    }

    @Override
    public List<DomainEvent> getEvents(String aggregateId) {
        List<EventDocument> documents = mongoTemplate.find(
            Query.query(Criteria.where("aggregateId").is(aggregateId)),
            EventDocument.class,
            "events"
        );

        return documents.stream()
            .map(doc -> new ConcreteDomainEvent(
                doc.getAggregateId(),
                doc.getType(),
                doc.getData(),
                doc.getTimestamp()))
            .collect(Collectors.toList());
    }

    @Override
    public List<DomainEvent> getEventsByType(String type) {
        List<EventDocument> documents = mongoTemplate.find(
            Query.query(Criteria.where("type").is(type)),
            EventDocument.class,
            "events"
        );

        return documents.stream()
            .map(doc -> new ConcreteDomainEvent(
                doc.getAggregateId(),
                doc.getType(),
                doc.getData(),
                doc.getTimestamp()))
            .collect(Collectors.toList());
    }

    @Override
    public List<DomainEvent> getEventsByTimeRange(LocalDateTime start, LocalDateTime end) {
        Query query = new Query(Criteria.where("timestamp")
            .gte(start)
            .lte(end));

        List<EventDocument> documents = mongoTemplate.find(query, EventDocument.class, "events");

        return documents.stream()
            .map(doc -> new ConcreteDomainEvent(
                doc.getAggregateId(),
                doc.getType(),
                doc.getData(),
                doc.getTimestamp()))
            .collect(Collectors.toList());
    }
}
