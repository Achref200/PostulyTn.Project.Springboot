package com.postulytn.job.event;

import com.postulytn.job.config.RabbitMQConfig;
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
public class JobEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishJobCreated(Long jobId, String title, Long companyId, String status) {
        JobEvent event = JobEvent.builder()
                .jobId(jobId)
                .title(title)
                .companyId(companyId)
                .status(status)
                .eventType(JobEvent.EventType.CREATED)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Publishing JOB_CREATED event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.JOB_EXCHANGE, RabbitMQConfig.JOB_CREATED_KEY, event);
    }
    
    public void publishJobPublished(Long jobId, String title, Long companyId) {
        JobEvent event = JobEvent.builder()
                .jobId(jobId)
                .title(title)
                .companyId(companyId)
                .status("PUBLISHED")
                .eventType(JobEvent.EventType.PUBLISHED)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Publishing JOB_PUBLISHED event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.JOB_EXCHANGE, RabbitMQConfig.JOB_PUBLISHED_KEY, event);
    }
    
    public void publishJobClosed(Long jobId, String title, Long companyId) {
        JobEvent event = JobEvent.builder()
                .jobId(jobId)
                .title(title)
                .companyId(companyId)
                .status("CLOSED")
                .eventType(JobEvent.EventType.CLOSED)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Publishing JOB_CLOSED event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.JOB_EXCHANGE, RabbitMQConfig.JOB_CLOSED_KEY, event);
    }
}
