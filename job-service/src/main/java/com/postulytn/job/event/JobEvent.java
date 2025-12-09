package com.postulytn.job.event;

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
public class JobEvent implements Serializable {
    
    private Long jobId;
    private String title;
    private Long companyId;
    private String status;
    private EventType eventType;
    private LocalDateTime timestamp;
    
    public enum EventType {
        CREATED, UPDATED, PUBLISHED, CLOSED, DELETED
    }
}
