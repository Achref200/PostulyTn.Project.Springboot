package com.postulytn.company.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "companies", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Company name is required")
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 100)
    private String sector;
    
    @Column(length = 1000)
    private String description;
    
    private String logoUrl;
    
    private String website;
    
    @Column(length = 200)
    private String location;
}
