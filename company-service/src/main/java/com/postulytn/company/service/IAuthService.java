package com.postulytn.company.service;

import com.postulytn.company.dto.AuthRequestDTO;
import com.postulytn.company.dto.AuthResponseDTO;
import com.postulytn.company.dto.RefreshTokenRequestDTO;
import com.postulytn.company.dto.RegisterRequestDTO;
import com.postulytn.company.dto.RecruiterDTO;

public interface IAuthService {
    AuthResponseDTO login(AuthRequestDTO request);
    RecruiterDTO register(RegisterRequestDTO request);
    AuthResponseDTO refreshToken(RefreshTokenRequestDTO request);
}
