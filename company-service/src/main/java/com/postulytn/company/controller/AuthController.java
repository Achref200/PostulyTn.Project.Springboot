package com.postulytn.company.controller;

import com.postulytn.company.dto.AuthRequestDTO;
import com.postulytn.company.dto.AuthResponseDTO;
import com.postulytn.company.dto.RecruiterDTO;
import com.postulytn.company.dto.RefreshTokenRequestDTO;
import com.postulytn.company.dto.RegisterRequestDTO;
import com.postulytn.company.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final IAuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/register")
    public ResponseEntity<RecruiterDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        RecruiterDTO created = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
