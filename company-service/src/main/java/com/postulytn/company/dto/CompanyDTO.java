package com.postulytn.company.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {
    private Long id;
    private String name;
    private String sector;
    private String location;
    private String website;
    private String logoUrl;
    // description is hidden - not exposed in API
}
