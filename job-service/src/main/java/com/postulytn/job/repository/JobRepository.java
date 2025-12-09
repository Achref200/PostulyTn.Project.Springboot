package com.postulytn.job.repository;

import com.postulytn.job.entity.Job;
import com.postulytn.job.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findByCompanyId(Long companyId);
    
    List<Job> findByStatus(JobStatus status);
    
    List<Job> findByCompanyIdAndStatus(Long companyId, JobStatus status);
}
