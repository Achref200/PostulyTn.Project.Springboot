package com.postulytn.job.dto;

import com.postulytn.job.entity.ContractType;
import com.postulytn.job.entity.JobStatus;
import com.postulytn.job.entity.Level;
import com.postulytn.job.model.Company;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDTO {
    private Long id;
    private String title;
    private String description;
    private String role;
    private Level level;
    private ContractType contractType;
    private String location;
    private JobStatus status;
    private Long companyId;
    
    // Transient field populated via sync call to Company Service
    private Company company;
}
