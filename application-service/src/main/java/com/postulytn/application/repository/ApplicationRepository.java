package com.postulytn.application.repository;

import com.postulytn.application.entity.ApplicationStatus;
import com.postulytn.application.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    List<JobApplication> findByJobId(Long jobId);
    
    List<JobApplication> findByCandidateId(Long candidateId);
    
    List<JobApplication> findByStatus(ApplicationStatus status);
    
    List<JobApplication> findByJobIdAndStatus(Long jobId, ApplicationStatus status);
}
