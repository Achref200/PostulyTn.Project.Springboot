package com.postulytn.application.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // External reference to candidate (could be a separate User service)
    @Column(nullable = false)
    private Long candidateId;
    
    private String cvUrl;
    
    @Column(length = 1000)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    
    // Reference to Job (stored as ID, per TP6 pattern)
    @Column(nullable = false)
    private Long jobId;
    
    @Column(nullable = false)
    private LocalDateTime applicationDate;
    
    @PrePersist
    protected void onCreate() {
        applicationDate = LocalDateTime.now();
        if (status == null) {
            status = ApplicationStatus.PENDING;
        }
    }
}
