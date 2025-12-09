package com.postulytn.application.event;

import com.postulytn.application.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class JobEventListener {
    
    @RabbitListener(queues = RabbitMQConfig.JOB_EVENTS_QUEUE)
    public void handleJobEvent(JobEvent event) {
        log.info("Received Job Event: {}", event);
        
        switch (event.getEventType()) {
            case CREATED:
                handleJobCreated(event);
                break;
            case PUBLISHED:
                handleJobPublished(event);
                break;
            case CLOSED:
                handleJobClosed(event);
                break;
            case DELETED:
                handleJobDeleted(event);
                break;
            default:
                log.info("Unhandled event type: {}", event.getEventType());
        }
    }
    
    private void handleJobCreated(JobEvent event) {
        log.info("Job created: {} - {}. Applications can now be submitted.", 
                event.getJobId(), event.getTitle());
    }
    
    private void handleJobPublished(JobEvent event) {
        log.info("Job published: {} - {}. Actively accepting applications!", 
                event.getJobId(), event.getTitle());
    }
    
    private void handleJobClosed(JobEvent event) {
        log.warn("Job closed: {}", event.getJobId());
    }

    private void handleJobDeleted(JobEvent event) {
        log.warn("Job deleted: {}", event.getJobId());
    }
}
