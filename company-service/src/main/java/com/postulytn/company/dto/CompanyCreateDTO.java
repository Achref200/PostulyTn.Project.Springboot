package com.postulytn.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCreateDTO {
    
    @NotBlank(message = "Company name is required")
    private String name;
    
    private String sector;
    private String description;
    private String logoUrl;
    private String website;
    private String location;
}
