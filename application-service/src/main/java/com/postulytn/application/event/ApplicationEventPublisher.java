package com.postulytn.application.event;

import com.postulytn.application.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class ApplicationEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishApplicationSubmitted(Long applicationId, Long jobId, Long candidateId, String status) {
        ApplicationEvent event = ApplicationEvent.builder()
                .applicationId(applicationId)
                .jobId(jobId)
                .candidateId(candidateId)
                .status(status)
                .eventType(ApplicationEvent.EventType.SUBMITTED)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Publishing APPLICATION_SUBMITTED event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.APPLICATION_EXCHANGE, RabbitMQConfig.APPLICATION_SUBMITTED_KEY, event);
    }
    
    public void publishApplicationStatusChanged(Long applicationId, Long jobId, Long candidateId, String newStatus) {
        ApplicationEvent event = ApplicationEvent.builder()
                .applicationId(applicationId)
                .jobId(jobId)
                .candidateId(candidateId)
                .status(newStatus)
                .eventType(ApplicationEvent.EventType.STATUS_CHANGED)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Publishing APPLICATION_STATUS_CHANGED event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.APPLICATION_EXCHANGE, RabbitMQConfig.APPLICATION_STATUS_CHANGED_KEY, event);
    }
}
