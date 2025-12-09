package com.postulytn.job.client;

import com.postulytn.job.model.Company;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for synchronous communication with Company Service.
 * Used to validate company existence and fetch company details.
 */
@FeignClient(name = "COMPANY-SERVICE")
public interface CompanyClient {
    
    @GetMapping("/companies/{id}")
    Company getCompanyById(@PathVariable("id") Long id);
    
    @GetMapping("/companies/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}
