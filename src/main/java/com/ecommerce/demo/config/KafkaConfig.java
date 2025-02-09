package com.ecommerce.demo.config;


import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.ecommerce.demo.model.OrderEvent;
import com.ecommerce.demo.model.ProductEvent;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;


@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, OrderEvent> orderEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getKafkaConfigs());
    }

    @Bean
    public ProducerFactory<String, ProductEvent> productEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getKafkaConfigs());
    }

    @Bean
    public KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate() {
        return new KafkaTemplate<>(orderEventProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, ProductEvent> productEventKafkaTemplate() {
        return new KafkaTemplate<>(productEventProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ecommerce-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    private Map<String, Object> getKafkaConfigs() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put(ProducerConfig.RETRIES_CONFIG, 3);
        configs.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        configs.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 60000);
        configs.put(ProducerConfig.ACKS_CONFIG, "all");
        return configs;
    }
}

// @Configuration
// @EnableKafka
// public class KafkaConfig {
//     @Bean
//     public ProducerFactory<String, OrderEvent> orderEventProducerFactory() {
//         Map<String, Object> configs = getKafkaConfigs();
//         return new DefaultKafkaProducerFactory<>(configs);
//     }

//     @Bean
//     public KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate() {
//         return new KafkaTemplate<>(orderEventProducerFactory());
//     }

//     @Bean
//     public ProducerFactory<String, ProductEvent> productEventProducerFactory() {
//         Map<String, Object> configs = getKafkaConfigs();
//         return new DefaultKafkaProducerFactory<>(configs);
//     }

//     @Bean
//     public KafkaTemplate<String, ProductEvent> productEventKafkaTemplate() {
//         return new KafkaTemplate<>(productEventProducerFactory());
//     }

//     private Map<String, Object> getKafkaConfigs() {
//         Map<String, Object> configs = new HashMap<>();
//         configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
//         configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//         configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//         return configs;
//     }
// }