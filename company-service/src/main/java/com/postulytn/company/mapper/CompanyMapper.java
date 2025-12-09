package com.postulytn.company.mapper;

import com.postulytn.company.dto.CompanyCreateDTO;
import com.postulytn.company.dto.CompanyDTO;
import com.postulytn.company.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {
    
    public CompanyDTO toDTO(Company entity) {
        if (entity == null) return null;
        return CompanyDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .sector(entity.getSector())
                .location(entity.getLocation())
                .website(entity.getWebsite())
                .logoUrl(entity.getLogoUrl())
                .build();
    }
    
    public Company toEntity(CompanyCreateDTO dto) {
        if (dto == null) return null;
        return Company.builder()
                .name(dto.getName())
                .sector(dto.getSector())
                .description(dto.getDescription())
                .logoUrl(dto.getLogoUrl())
                .website(dto.getWebsite())
                .location(dto.getLocation())
                .build();
    }
    
    public void updateEntity(Company entity, CompanyCreateDTO dto) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getSector() != null) entity.setSector(dto.getSector());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getLogoUrl() != null) entity.setLogoUrl(dto.getLogoUrl());
        if (dto.getWebsite() != null) entity.setWebsite(dto.getWebsite());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
    }
}
