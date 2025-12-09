package com.postulytn.job.dto;

import com.postulytn.job.entity.ContractType;
import com.postulytn.job.entity.JobStatus;
import com.postulytn.job.entity.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCreateDTO {
    
    @NotBlank(message = "Job title is required")
    private String title;
    
    private String description;
    private String role;
    private Level level;
    private ContractType contractType;
    private String location;
    private JobStatus status;
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
}
