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
public class CompanyEvent implements Serializable {
    
    private Long companyId;
    private String companyName;
    private String sector;
    private String location;
    private EventType eventType;
    private LocalDateTime timestamp;
    
    public enum EventType {
        CREATED, UPDATED, DELETED
    }
}
