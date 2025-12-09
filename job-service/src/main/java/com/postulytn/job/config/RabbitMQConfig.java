package com.postulytn.job.config;

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

    public static final String JOB_EXCHANGE = "job.exchange";
    public static final String JOB_CREATED_QUEUE = "job.created.queue";
    public static final String JOB_PUBLISHED_QUEUE = "job.published.queue";
    public static final String JOB_CLOSED_QUEUE = "job.closed.queue";
    public static final String JOB_CREATED_KEY = "job.created";
    public static final String JOB_PUBLISHED_KEY = "job.published";
    public static final String JOB_CLOSED_KEY = "job.closed";

    public static final String COMPANY_EXCHANGE = "company.exchange";
    public static final String COMPANY_EVENTS_QUEUE = "job-service.company.events.queue";
    public static final String COMPANY_ALL_EVENTS_KEY = "company.*";

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(JOB_EXCHANGE);
    }
    
    @Bean
    public Queue jobCreatedQueue() {
        return QueueBuilder.durable(JOB_CREATED_QUEUE).build();
    }
    
    @Bean
    public Queue jobPublishedQueue() {
        return QueueBuilder.durable(JOB_PUBLISHED_QUEUE).build();
    }
    
    @Bean
    public Queue jobClosedQueue() {
        return QueueBuilder.durable(JOB_CLOSED_QUEUE).build();
    }
    
    @Bean
    public Binding jobCreatedBinding(Queue jobCreatedQueue, TopicExchange jobExchange) {
        return BindingBuilder.bind(jobCreatedQueue).to(jobExchange).with(JOB_CREATED_KEY);
    }
    
    @Bean
    public Binding jobPublishedBinding(Queue jobPublishedQueue, TopicExchange jobExchange) {
        return BindingBuilder.bind(jobPublishedQueue).to(jobExchange).with(JOB_PUBLISHED_KEY);
    }
    
    @Bean
    public Binding jobClosedBinding(Queue jobClosedQueue, TopicExchange jobExchange) {
        return BindingBuilder.bind(jobClosedQueue).to(jobExchange).with(JOB_CLOSED_KEY);
    }

    @Bean
    public TopicExchange companyExchange() {
        return new TopicExchange(COMPANY_EXCHANGE);
    }
    
    @Bean
    public Queue companyEventsQueue() {
        return QueueBuilder.durable(COMPANY_EVENTS_QUEUE).build();
    }
    
    @Bean
    public Binding companyEventsBinding(Queue companyEventsQueue, TopicExchange companyExchange) {
        return BindingBuilder.bind(companyEventsQueue).to(companyExchange).with(COMPANY_ALL_EVENTS_KEY);
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
