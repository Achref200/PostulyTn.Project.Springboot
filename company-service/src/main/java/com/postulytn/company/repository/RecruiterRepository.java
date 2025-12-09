package com.postulytn.company.repository;

import com.postulytn.company.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    
    Optional<Recruiter> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Recruiter> findByCompanyId(Long companyId);
}
