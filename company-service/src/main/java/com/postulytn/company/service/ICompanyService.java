package com.postulytn.company.service;

import com.postulytn.company.dto.CompanyCreateDTO;
import com.postulytn.company.dto.CompanyDTO;

import java.util.List;

public interface ICompanyService {
    CompanyDTO createCompany(CompanyCreateDTO dto);
    CompanyDTO getCompanyById(Long id);
    CompanyDTO getCompanyByName(String name);
    List<CompanyDTO> getAllCompanies();
    CompanyDTO updateCompany(Long id, CompanyCreateDTO dto);
    void deleteCompany(Long id);
    boolean existsById(Long id);
}
