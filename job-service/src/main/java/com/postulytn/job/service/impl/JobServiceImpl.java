package com.postulytn.job.service.impl;

import com.postulytn.job.client.CompanyClient;
import com.postulytn.job.dto.JobCreateDTO;
import com.postulytn.job.event.JobEventPublisher;
import com.postulytn.job.dto.JobDTO;
import com.postulytn.job.entity.Job;
import com.postulytn.job.entity.JobStatus;
import com.postulytn.job.exception.ResourceNotFoundException;
import com.postulytn.job.mapper.JobMapper;
import com.postulytn.job.model.Company;
import com.postulytn.job.repository.JobRepository;
import com.postulytn.job.service.IJobService;
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
public class JobServiceImpl implements IJobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final CompanyClient companyClient;
    private final Optional<JobEventPublisher> eventPublisher;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper, CompanyClient companyClient,
                          @Autowired(required = false) JobEventPublisher eventPublisher) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.companyClient = companyClient;
        this.eventPublisher = Optional.ofNullable(eventPublisher);
    }
    
    @Override
    public JobDTO createJob(JobCreateDTO dto) {
        log.info("Validating company with ID: {}", dto.getCompanyId());
        Boolean exists = companyClient.existsById(dto.getCompanyId());
        if (exists == null || !exists) {
            throw new ResourceNotFoundException("Company not found with id: " + dto.getCompanyId());
        }
        
        Job job = jobMapper.toEntity(dto);
        Job saved = jobRepository.save(job);
        
        eventPublisher.ifPresent(ep -> ep.publishJobCreated(saved.getId(), saved.getTitle(), saved.getCompanyId(), saved.getStatus().name()));
        log.info("Job created: {}", saved.getId());
        
        return jobMapper.toDTO(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return jobMapper.toDTO(job);
    }
    
    @Override
    @Transactional(readOnly = true)
    public JobDTO getJobByIdWithCompany(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        
        JobDTO dto = jobMapper.toDTO(job);
        
        try {
            Company company = companyClient.getCompanyById(job.getCompanyId());
            dto.setCompany(company);
        } catch (Exception e) {
            log.warn("Could not fetch company details for job {}: {}", id, e.getMessage());
        }
        
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsByCompanyId(Long companyId) {
        return jobRepository.findByCompanyId(companyId).stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JobDTO> getJobsByStatus(JobStatus status) {
        return jobRepository.findByStatus(status).stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public JobDTO updateJob(Long id, JobCreateDTO dto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        
        jobMapper.updateEntity(job, dto);
        Job updated = jobRepository.save(job);
        
        if (updated.getStatus() == JobStatus.PUBLISHED) {
            eventPublisher.ifPresent(ep -> ep.publishJobPublished(updated.getId(), updated.getTitle(), updated.getCompanyId()));
        } else if (updated.getStatus() == JobStatus.CLOSED) {
            eventPublisher.ifPresent(ep -> ep.publishJobClosed(updated.getId(), updated.getTitle(), updated.getCompanyId()));
        }
        log.info("Job updated: {}", updated.getId());
        
        return jobMapper.toDTO(updated);
    }
    
    @Override
    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        
        jobRepository.deleteById(id);
        
        eventPublisher.ifPresent(ep -> ep.publishJobClosed(id, job.getTitle(), job.getCompanyId()));
        log.info("Job deleted: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return jobRepository.existsById(id);
    }
}
