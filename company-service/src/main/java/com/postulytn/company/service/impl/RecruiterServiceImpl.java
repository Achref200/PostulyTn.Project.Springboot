package com.postulytn.company.service.impl;

import com.postulytn.company.dto.RecruiterDTO;
import com.postulytn.company.entity.Recruiter;
import com.postulytn.company.exception.ResourceNotFoundException;
import com.postulytn.company.mapper.RecruiterMapper;
import com.postulytn.company.repository.RecruiterRepository;
import com.postulytn.company.service.IRecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterServiceImpl implements IRecruiterService {
    
    private final RecruiterRepository recruiterRepository;
    private final RecruiterMapper recruiterMapper;
    
    @Override
    @Transactional(readOnly = true)
    public RecruiterDTO getRecruiterById(Long id) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + id));
        return recruiterMapper.toDTO(recruiter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RecruiterDTO getRecruiterByEmail(String email) {
        Recruiter recruiter = recruiterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + email));
        return recruiterMapper.toDTO(recruiter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RecruiterDTO> getAllRecruiters() {
        return recruiterRepository.findAll().stream()
                .map(recruiterMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RecruiterDTO> getRecruitersByCompanyId(Long companyId) {
        return recruiterRepository.findByCompanyId(companyId).stream()
                .map(recruiterMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public RecruiterDTO updateRecruiter(Long id, RecruiterDTO dto) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with id: " + id));
        
        if (dto.getName() != null) recruiter.setName(dto.getName());
        if (dto.getEmail() != null) recruiter.setEmail(dto.getEmail());
        if (dto.getRole() != null) recruiter.setRole(dto.getRole());
        
        Recruiter updated = recruiterRepository.save(recruiter);
        return recruiterMapper.toDTO(updated);
    }
    
    @Override
    public void deleteRecruiter(Long id) {
        if (!recruiterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recruiter not found with id: " + id);
        }
        recruiterRepository.deleteById(id);
    }
}
