package com.postulytn.job.event;

import com.postulytn.job.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class CompanyEventListener {
    
    @RabbitListener(queues = RabbitMQConfig.COMPANY_EVENTS_QUEUE)
    public void handleCompanyEvent(CompanyEvent event) {
        log.info("Received Company Event: {}", event);
        
        switch (event.getEventType()) {
            case CREATED:
                handleCompanyCreated(event);
                break;
            case UPDATED:
                handleCompanyUpdated(event);
                break;
            case DELETED:
                handleCompanyDeleted(event);
                break;
        }
    }
    
    private void handleCompanyCreated(CompanyEvent event) {
        log.info("Company created: {} - {}", event.getCompanyId(), event.getCompanyName());
    }
    
    private void handleCompanyUpdated(CompanyEvent event) {
        log.info("Company updated: {} - {}", event.getCompanyId(), event.getCompanyName());
    }
    
    private void handleCompanyDeleted(CompanyEvent event) {
        log.warn("Company deleted: {}", event.getCompanyId());
    }
}
