package com.postulytn.application.mapper;

import com.postulytn.application.dto.ApplicationCreateDTO;
import com.postulytn.application.dto.ApplicationDTO;
import com.postulytn.application.entity.ApplicationStatus;
import com.postulytn.application.entity.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {
    
    public ApplicationDTO toDTO(JobApplication entity) {
        if (entity == null) return null;
        return ApplicationDTO.builder()
                .id(entity.getId())
                .candidateId(entity.getCandidateId())
                .cvUrl(entity.getCvUrl())
                .notes(entity.getNotes())
                .status(entity.getStatus())
                .jobId(entity.getJobId())
                .applicationDate(entity.getApplicationDate())
                .build();
    }
    
    public JobApplication toEntity(ApplicationCreateDTO dto) {
        if (dto == null) return null;
        return JobApplication.builder()
                .candidateId(dto.getCandidateId())
                .cvUrl(dto.getCvUrl())
                .notes(dto.getNotes())
                .status(ApplicationStatus.PENDING)
                .jobId(dto.getJobId())
                .build();
    }
}
