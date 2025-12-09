package com.postulytn.job.model;

import lombok.*;

/**
 * Non-entity model class for Company data fetched from Company Service.
 * Used as @Transient field in Job responses (per TP6 pattern).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    private Long id;
    private String name;
    private String sector;
    private String location;
    private String website;
    private String logoUrl;
}
