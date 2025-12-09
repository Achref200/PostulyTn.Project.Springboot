package com.postulytn.application.service.impl;

import com.postulytn.application.client.JobClient;
import com.postulytn.application.dto.ApplicationCreateDTO;
import com.postulytn.application.event.ApplicationEventPublisher;
import com.postulytn.application.dto.ApplicationDTO;
import com.postulytn.application.dto.StatusUpdateDTO;
import com.postulytn.application.entity.ApplicationStatus;
import com.postulytn.application.entity.JobApplication;
import com.postulytn.application.exception.ResourceNotFoundException;
import com.postulytn.application.mapper.ApplicationMapper;
import com.postulytn.application.model.Job;
import com.postulytn.application.repository.ApplicationRepository;
import com.postulytn.application.service.IApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ApplicationServiceImpl implements IApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final JobClient jobClient;
    private final Optional<ApplicationEventPublisher> eventPublisher;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository, ApplicationMapper applicationMapper,
                                  JobClient jobClient, @Autowired(required = false) ApplicationEventPublisher eventPublisher) {
        this.applicationRepository = applicationRepository;
        this.applicationMapper = applicationMapper;
        this.jobClient = jobClient;
        this.eventPublisher = Optional.ofNullable(eventPublisher);
    }
    
    @Override
    public ApplicationDTO createApplication(ApplicationCreateDTO dto) {
        log.info("Validating job with ID: {}", dto.getJobId());
        Boolean exists = jobClient.existsById(dto.getJobId());
        if (exists == null || !exists) {
            throw new ResourceNotFoundException("Job not found with id: " + dto.getJobId());
        }
        
        JobApplication application = applicationMapper.toEntity(dto);
        JobApplication saved = applicationRepository.save(application);
        
        eventPublisher.ifPresent(ep -> ep.publishApplicationSubmitted(saved.getId(), saved.getJobId(), saved.getCandidateId(), saved.getStatus().name()));
        log.info("Application submitted: {}", saved.getId());
        
        return applicationMapper.toDTO(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationById(Long id) {
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        return applicationMapper.toDTO(application);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationByIdWithJob(Long id) {
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        
        ApplicationDTO dto = applicationMapper.toDTO(application);
        
        try {
            Job job = jobClient.getJobById(application.getJobId());
            dto.setJob(job);
        } catch (Exception e) {
            log.warn("Could not fetch job details for application {}: {}", id, e.getMessage());
        }
        
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByJobId(Long jobId) {
        return applicationRepository.findByJobId(jobId).stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByCandidateId(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId).stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status).stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ApplicationDTO updateApplicationStatus(Long id, StatusUpdateDTO dto) {
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        
        application.setStatus(dto.getStatus());
        if (dto.getNotes() != null) {
            application.setNotes(dto.getNotes());
        }
        
        JobApplication updated = applicationRepository.save(application);
        
        eventPublisher.ifPresent(ep -> ep.publishApplicationStatusChanged(updated.getId(), updated.getJobId(), updated.getCandidateId(), updated.getStatus().name()));
        log.info("Application status changed: {}", updated.getId());
        
        return applicationMapper.toDTO(updated);
    }
    
    @Override
    public ApplicationDTO updateApplication(Long id, ApplicationCreateDTO dto) {
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        
        if (dto.getCvUrl() != null) application.setCvUrl(dto.getCvUrl());
        if (dto.getNotes() != null) application.setNotes(dto.getNotes());
        
        JobApplication updated = applicationRepository.save(application);
        return applicationMapper.toDTO(updated);
    }
    
    @Override
    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found with id: " + id);
        }
        applicationRepository.deleteById(id);
    }
}
