package com.postulytn.application.client;

import com.postulytn.application.model.Job;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for synchronous communication with Job Service.
 * Used to validate job existence and fetch job details.
 */
@FeignClient(name = "JOB-SERVICE")
public interface JobClient {
    
    @GetMapping("/jobs/{id}")
    Job getJobById(@PathVariable("id") Long id);
    
    @GetMapping("/jobs/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}
