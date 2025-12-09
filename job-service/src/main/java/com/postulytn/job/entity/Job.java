package com.postulytn.job.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Job title is required")
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 100)
    private String role;
    
    @Enumerated(EnumType.STRING)
    private Level level;
    
    @Enumerated(EnumType.STRING)
    private ContractType contractType;
    
    @Column(length = 200)
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;
    
    // Reference to Company (stored as ID, per TP6 pattern)
    @Column(nullable = false)
    private Long companyId;
}
