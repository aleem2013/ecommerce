package com.ecommerce.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.ecommerce.demo.component.MongoEventStore;
import com.ecommerce.demo.model.EventStore;

import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
@EnableJpaAuditing
public class EventSourcingConfig {

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory, MongoMappingContext context) {
        MappingMongoConverter converter = new MappingMongoConverter(
            new DefaultDbRefResolver(mongoDbFactory), context);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(mongoDbFactory, converter);
    }

    @Bean
    public EventStore eventStore(MongoTemplate mongoTemplate) {
        return new MongoEventStore(mongoTemplate);
    }
}
