package com.ecommerce.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue notificationQueue() {
        return new Queue("notification-queue", true);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange("notification-exchange");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
            .bind(notificationQueue())
            .to(notificationExchange())
            .with("order.notification");
    }
}
