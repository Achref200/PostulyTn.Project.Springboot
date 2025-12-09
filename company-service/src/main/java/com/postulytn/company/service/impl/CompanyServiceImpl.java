package com.postulytn.company.service.impl;

import com.postulytn.company.dto.CompanyCreateDTO;
import com.postulytn.company.dto.CompanyDTO;
import com.postulytn.company.entity.Company;
import com.postulytn.company.event.CompanyEventPublisher;
import com.postulytn.company.exception.DuplicateResourceException;
import com.postulytn.company.exception.ResourceNotFoundException;
import com.postulytn.company.mapper.CompanyMapper;
import com.postulytn.company.repository.CompanyRepository;
import com.postulytn.company.service.ICompanyService;
import lombok.RequiredArgsConstructor;
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
public class CompanyServiceImpl implements ICompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final Optional<CompanyEventPublisher> eventPublisher;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper,
                              @Autowired(required = false) CompanyEventPublisher eventPublisher) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.eventPublisher = Optional.ofNullable(eventPublisher);
    }
    
    @Override
    public CompanyDTO createCompany(CompanyCreateDTO dto) {
        if (companyRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Company with name '" + dto.getName() + "' already exists");
        }
        Company company = companyMapper.toEntity(dto);
        Company saved = companyRepository.save(company);
        
        eventPublisher.ifPresent(ep -> ep.publishCompanyCreated(saved.getId(), saved.getName(), saved.getSector(), saved.getLocation()));
        log.info("Company created: {}", saved.getId());
        
        return companyMapper.toDTO(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return companyMapper.toDTO(company);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyByName(String name) {
        Company company = companyRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with name: " + name));
        return companyMapper.toDTO(company);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CompanyDTO updateCompany(Long id, CompanyCreateDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        
        if (dto.getName() != null && !dto.getName().equals(company.getName()) 
                && companyRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Company with name '" + dto.getName() + "' already exists");
        }
        
        companyMapper.updateEntity(company, dto);
        Company updated = companyRepository.save(company);
        
        eventPublisher.ifPresent(ep -> ep.publishCompanyUpdated(updated.getId(), updated.getName(), updated.getSector(), updated.getLocation()));
        log.info("Company updated: {}", updated.getId());
        
        return companyMapper.toDTO(updated);
    }
    
    @Override
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        
        String companyName = company.getName();
        companyRepository.deleteById(id);
        
        eventPublisher.ifPresent(ep -> ep.publishCompanyDeleted(id, companyName));
        log.info("Company deleted: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return companyRepository.existsById(id);
    }
}
