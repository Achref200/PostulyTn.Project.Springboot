package com.postulytn.application.model;

import lombok.*;

/**
 * Non-entity model class for Job data fetched from Job Service.
 * Used as @Transient field in Application responses (per TP6 pattern).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    private Long id;
    private String title;
    private String description;
    private String role;
    private String level;
    private String contractType;
    private String location;
    private String status;
    private Long companyId;
}
