package com.postulytn.application.dto;

import com.postulytn.application.entity.ApplicationStatus;
import com.postulytn.application.model.Job;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDTO {
    private Long id;
    private Long candidateId;
    private String cvUrl;
    private String notes;
    private ApplicationStatus status;
    private Long jobId;
    private LocalDateTime applicationDate;
    
    // Transient field populated via sync call to Job Service
    private Job job;
}
