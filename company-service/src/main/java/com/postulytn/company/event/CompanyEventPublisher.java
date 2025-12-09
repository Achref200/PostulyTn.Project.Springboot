package com.postulytn.company.event;

import com.postulytn.company.config.RabbitMQConfig;
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
public class CompanyEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishCompanyCreated(Long companyId, String companyName, String sector, String location) {
        CompanyEvent event = CompanyEvent.builder()
                .companyId(companyId)
                .companyName(companyName)
                .sector(sector)
                .location(location)
                .eventType(CompanyEvent.EventType.CREATED)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Publishing COMPANY_CREATED event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.COMPANY_EXCHANGE, RabbitMQConfig.COMPANY_CREATED_KEY, event);
    }
    
    public void publishCompanyUpdated(Long companyId, String companyName, String sector, String location) {
        CompanyEvent event = CompanyEvent.builder()
                .companyId(companyId)
                .companyName(companyName)
                .sector(sector)
                .location(location)
                .eventType(CompanyEvent.EventType.UPDATED)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Publishing COMPANY_UPDATED event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.COMPANY_EXCHANGE, RabbitMQConfig.COMPANY_UPDATED_KEY, event);
    }
    
    public void publishCompanyDeleted(Long companyId, String companyName) {
        CompanyEvent event = CompanyEvent.builder()
                .companyId(companyId)
                .companyName(companyName)
                .eventType(CompanyEvent.EventType.DELETED)
                .timestamp(LocalDateTime.now())
                .build();
        
        log.info("Publishing COMPANY_DELETED event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.COMPANY_EXCHANGE, RabbitMQConfig.COMPANY_DELETED_KEY, event);
    }
}
