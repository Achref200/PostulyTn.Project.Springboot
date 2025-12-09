package com.postulytn.company.service;

import com.postulytn.company.dto.RecruiterDTO;

import java.util.List;

public interface IRecruiterService {
    RecruiterDTO getRecruiterById(Long id);
    RecruiterDTO getRecruiterByEmail(String email);
    List<RecruiterDTO> getAllRecruiters();
    List<RecruiterDTO> getRecruitersByCompanyId(Long companyId);
    RecruiterDTO updateRecruiter(Long id, RecruiterDTO dto);
    void deleteRecruiter(Long id);
}
