package com.postulytn.company.mapper;

import com.postulytn.company.dto.RecruiterDTO;
import com.postulytn.company.entity.Recruiter;
import org.springframework.stereotype.Component;

@Component
public class RecruiterMapper {
    
    public RecruiterDTO toDTO(Recruiter entity) {
        if (entity == null) return null;
        return RecruiterDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .companyId(entity.getCompany() != null ? entity.getCompany().getId() : null)
                .companyName(entity.getCompany() != null ? entity.getCompany().getName() : null)
                .build();
    }
}
