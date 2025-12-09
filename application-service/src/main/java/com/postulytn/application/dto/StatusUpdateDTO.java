package com.postulytn.application.dto;

import com.postulytn.application.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateDTO {
    
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
    
    private String notes;
}
