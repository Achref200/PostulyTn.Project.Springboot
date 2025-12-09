package com.postulytn.application.config;

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

    public static final String APPLICATION_EXCHANGE = "application.exchange";
    public static final String APPLICATION_SUBMITTED_QUEUE = "application.submitted.queue";
    public static final String APPLICATION_STATUS_CHANGED_QUEUE = "application.status.changed.queue";
    public static final String APPLICATION_SUBMITTED_KEY = "application.submitted";
    public static final String APPLICATION_STATUS_CHANGED_KEY = "application.status.changed";

    public static final String JOB_EXCHANGE = "job.exchange";
    public static final String JOB_EVENTS_QUEUE = "application-service.job.events.queue";
    public static final String JOB_ALL_EVENTS_KEY = "job.*";

    @Bean
    public TopicExchange applicationExchange() {
        return new TopicExchange(APPLICATION_EXCHANGE);
    }
    
    @Bean
    public Queue applicationSubmittedQueue() {
        return QueueBuilder.durable(APPLICATION_SUBMITTED_QUEUE).build();
    }
    
    @Bean
    public Queue applicationStatusChangedQueue() {
        return QueueBuilder.durable(APPLICATION_STATUS_CHANGED_QUEUE).build();
    }
    
    @Bean
    public Binding applicationSubmittedBinding(Queue applicationSubmittedQueue, TopicExchange applicationExchange) {
        return BindingBuilder.bind(applicationSubmittedQueue).to(applicationExchange).with(APPLICATION_SUBMITTED_KEY);
    }
    
    @Bean
    public Binding applicationStatusChangedBinding(Queue applicationStatusChangedQueue, TopicExchange applicationExchange) {
        return BindingBuilder.bind(applicationStatusChangedQueue).to(applicationExchange).with(APPLICATION_STATUS_CHANGED_KEY);
    }

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(JOB_EXCHANGE);
    }
    
    @Bean
    public Queue jobEventsQueue() {
        return QueueBuilder.durable(JOB_EVENTS_QUEUE).build();
    }
    
    @Bean
    public Binding jobEventsBinding(Queue jobEventsQueue, TopicExchange jobExchange) {
        return BindingBuilder.bind(jobEventsQueue).to(jobExchange).with(JOB_ALL_EVENTS_KEY);
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
