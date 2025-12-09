package com.postulytn.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCreateDTO {
    
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
    
    private String cvUrl;
    private String notes;
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
}
