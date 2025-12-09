package com.postulytn.job.service;

import com.postulytn.job.dto.JobCreateDTO;
import com.postulytn.job.dto.JobDTO;
import com.postulytn.job.entity.JobStatus;

import java.util.List;

public interface IJobService {
    JobDTO createJob(JobCreateDTO dto);
    JobDTO getJobById(Long id);
    JobDTO getJobByIdWithCompany(Long id);
    List<JobDTO> getAllJobs();
    List<JobDTO> getJobsByCompanyId(Long companyId);
    List<JobDTO> getJobsByStatus(JobStatus status);
    JobDTO updateJob(Long id, JobCreateDTO dto);
    void deleteJob(Long id);
    boolean existsById(Long id);
}
