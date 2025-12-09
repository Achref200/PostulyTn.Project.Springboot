package com.postulytn.job.mapper;

import com.postulytn.job.dto.JobCreateDTO;
import com.postulytn.job.dto.JobDTO;
import com.postulytn.job.entity.Job;
import com.postulytn.job.entity.JobStatus;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {
    
    public JobDTO toDTO(Job entity) {
        if (entity == null) return null;
        return JobDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .role(entity.getRole())
                .level(entity.getLevel())
                .contractType(entity.getContractType())
                .location(entity.getLocation())
                .status(entity.getStatus())
                .companyId(entity.getCompanyId())
                .build();
    }
    
    public Job toEntity(JobCreateDTO dto) {
        if (dto == null) return null;
        return Job.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .role(dto.getRole())
                .level(dto.getLevel())
                .contractType(dto.getContractType())
                .location(dto.getLocation())
                .status(dto.getStatus() != null ? dto.getStatus() : JobStatus.DRAFT)
                .companyId(dto.getCompanyId())
                .build();
    }
    
    public void updateEntity(Job entity, JobCreateDTO dto) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getRole() != null) entity.setRole(dto.getRole());
        if (dto.getLevel() != null) entity.setLevel(dto.getLevel());
        if (dto.getContractType() != null) entity.setContractType(dto.getContractType());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
    }
}
