package com.postulytn.company.service.impl;

import com.postulytn.company.dto.AuthRequestDTO;
import com.postulytn.company.dto.AuthResponseDTO;
import com.postulytn.company.dto.RecruiterDTO;
import com.postulytn.company.dto.RefreshTokenRequestDTO;
import com.postulytn.company.dto.RegisterRequestDTO;
import com.postulytn.company.entity.Company;
import com.postulytn.company.entity.Recruiter;
import com.postulytn.company.entity.Role;
import com.postulytn.company.exception.DuplicateResourceException;
import com.postulytn.company.exception.ResourceNotFoundException;
import com.postulytn.company.mapper.RecruiterMapper;
import com.postulytn.company.repository.CompanyRepository;
import com.postulytn.company.repository.RecruiterRepository;
import com.postulytn.company.security.JwtUtil;
import com.postulytn.company.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements IAuthService {
    
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RecruiterMapper recruiterMapper;
    
    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        Recruiter recruiter = recruiterRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), recruiter.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        
        Long companyId = recruiter.getCompany() != null ? recruiter.getCompany().getId() : null;
        String token = jwtUtil.generateToken(
                recruiter.getEmail(),
                recruiter.getRole().name(),
                recruiter.getId(),
                companyId
        );
        
        String refreshToken = jwtUtil.generateRefreshToken(
                recruiter.getEmail(),
                recruiter.getRole().name(),
                recruiter.getId(),
                companyId
        );
        
        return AuthResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .recruiterId(recruiter.getId())
                .email(recruiter.getEmail())
                .role(recruiter.getRole().name())
                .companyId(companyId)
                .build();
    }
    
    @Override
    public RecruiterDTO register(RegisterRequestDTO request) {
        if (recruiterRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        
        Company company = null;
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));
        }
        
        Recruiter recruiter = Recruiter.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.RECRUITER)
                .company(company)
                .build();
        
        Recruiter saved = recruiterRepository.save(recruiter);
        return recruiterMapper.toDTO(saved);
    }
    
    @Override
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        
        // Validate refresh token
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
        
        // Verify it's actually a refresh token
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Token is not a refresh token");
        }
        
        // Extract user info from refresh token
        String email = jwtUtil.extractUsername(refreshToken);
        Recruiter recruiter = recruiterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        
        Long companyId = recruiter.getCompany() != null ? recruiter.getCompany().getId() : null;
        
        // Generate new access token
        String newToken = jwtUtil.generateToken(
                recruiter.getEmail(),
                recruiter.getRole().name(),
                recruiter.getId(),
                companyId
        );
        
        // Generate new refresh token
        String newRefreshToken = jwtUtil.generateRefreshToken(
                recruiter.getEmail(),
                recruiter.getRole().name(),
                recruiter.getId(),
                companyId
        );
        
        return AuthResponseDTO.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .recruiterId(recruiter.getId())
                .email(recruiter.getEmail())
                .role(recruiter.getRole().name())
                .companyId(companyId)
                .build();
    }
}
