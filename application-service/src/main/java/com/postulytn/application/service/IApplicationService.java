package com.postulytn.application.service;

import com.postulytn.application.dto.ApplicationCreateDTO;
import com.postulytn.application.dto.ApplicationDTO;
import com.postulytn.application.dto.StatusUpdateDTO;
import com.postulytn.application.entity.ApplicationStatus;

import java.util.List;

public interface IApplicationService {
    ApplicationDTO createApplication(ApplicationCreateDTO dto);
    ApplicationDTO getApplicationById(Long id);
    ApplicationDTO getApplicationByIdWithJob(Long id);
    List<ApplicationDTO> getAllApplications();
    List<ApplicationDTO> getApplicationsByJobId(Long jobId);
    List<ApplicationDTO> getApplicationsByCandidateId(Long candidateId);
    List<ApplicationDTO> getApplicationsByStatus(ApplicationStatus status);
    ApplicationDTO updateApplicationStatus(Long id, StatusUpdateDTO dto);
    ApplicationDTO updateApplication(Long id, ApplicationCreateDTO dto);
    void deleteApplication(Long id);
}
