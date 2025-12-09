package com.postulytn.application.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEvent implements Serializable {
    
    private Long applicationId;
    private Long jobId;
    private Long candidateId;
    private String status;
    private EventType eventType;
    private LocalDateTime timestamp;
    
    public enum EventType {
        SUBMITTED, STATUS_CHANGED, WITHDRAWN
    }
}
