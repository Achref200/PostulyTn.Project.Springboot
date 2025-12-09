package com.postulytn.company.dto;

import com.postulytn.company.entity.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Long companyId;
    private String companyName;
    // password is hidden - never exposed in API
}
