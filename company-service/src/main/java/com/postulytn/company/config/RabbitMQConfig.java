package com.postulytn.company.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQConfig {

    public static final String COMPANY_EXCHANGE = "company.exchange";
    public static final String COMPANY_CREATED_QUEUE = "company.created.queue";
    public static final String COMPANY_UPDATED_QUEUE = "company.updated.queue";
    public static final String COMPANY_DELETED_QUEUE = "company.deleted.queue";
    public static final String COMPANY_CREATED_KEY = "company.created";
    public static final String COMPANY_UPDATED_KEY = "company.updated";
    public static final String COMPANY_DELETED_KEY = "company.deleted";

    @Bean
    public TopicExchange companyExchange() {
        return new TopicExchange(COMPANY_EXCHANGE);
    }

    @Bean
    public Queue companyCreatedQueue() {
        return QueueBuilder.durable(COMPANY_CREATED_QUEUE).build();
    }
    
    @Bean
    public Queue companyUpdatedQueue() {
        return QueueBuilder.durable(COMPANY_UPDATED_QUEUE).build();
    }
    
    @Bean
    public Queue companyDeletedQueue() {
        return QueueBuilder.durable(COMPANY_DELETED_QUEUE).build();
    }

    @Bean
    public Binding companyCreatedBinding(Queue companyCreatedQueue, TopicExchange companyExchange) {
        return BindingBuilder.bind(companyCreatedQueue).to(companyExchange).with(COMPANY_CREATED_KEY);
    }
    
    @Bean
    public Binding companyUpdatedBinding(Queue companyUpdatedQueue, TopicExchange companyExchange) {
        return BindingBuilder.bind(companyUpdatedQueue).to(companyExchange).with(COMPANY_UPDATED_KEY);
    }
    
    @Bean
    public Binding companyDeletedBinding(Queue companyDeletedQueue, TopicExchange companyExchange) {
        return BindingBuilder.bind(companyDeletedQueue).to(companyExchange).with(COMPANY_DELETED_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
