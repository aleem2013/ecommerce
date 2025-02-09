package com.ecommerce.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.Product;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;



@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Product> productRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Product> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(
            JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
        );
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        return template;
    }

    @Bean
    public RedisTemplate<String, Order> orderRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Order> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(
            JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
        );
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        return template;
    }
}


// @Configuration
// public class RedisConfig {
//     @Bean
//     public RedisTemplate<String, Product> productRedisTemplate(RedisConnectionFactory connectionFactory) {
//         RedisTemplate<String, Product> template = new RedisTemplate<>();
//         template.setConnectionFactory(connectionFactory);
//         template.setKeySerializer(new StringRedisSerializer());
//         template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Product.class));
//         return template;
//     }

//     @Bean
//     public RedisTemplate<String, Order> orderRedisTemplate(RedisConnectionFactory connectionFactory) {
//         RedisTemplate<String, Order> template = new RedisTemplate<>();
//         template.setConnectionFactory(connectionFactory);
//         template.setKeySerializer(new StringRedisSerializer());
//         template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Order.class));
//         return template;
//     }
// }

// @Configuration
// public class RedisConfig {
//     @Bean
//     public RedisTemplate<String, Product> redisTemplate(RedisConnectionFactory connectionFactory) {
//         RedisTemplate<String, Product> template = new RedisTemplate<>();
//         template.setConnectionFactory(connectionFactory);
//         template.setKeySerializer(new StringRedisSerializer());
//         template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Product.class));
//         template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Product.class));
//         return template;
//     }
// }