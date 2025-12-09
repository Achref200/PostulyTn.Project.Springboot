package com.postulytn.company.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(String email, String role, Long recruiterId, Long companyId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("recruiterId", recruiterId);
        claims.put("companyId", companyId);
        
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    
    public Long extractRecruiterId(String token) {
        Object recruiterId = extractAllClaims(token).get("recruiterId");
        return recruiterId != null ? ((Number) recruiterId).longValue() : null;
    }
    
    public Long extractCompanyId(String token) {
        Object companyId = extractAllClaims(token).get("companyId");
        return companyId != null ? ((Number) companyId).longValue() : null;
    }
    
    public String generateRefreshToken(String email, String role, Long recruiterId, Long companyId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("recruiterId", recruiterId);
        claims.put("companyId", companyId);
        claims.put("tokenType", "refresh");
        
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = extractAllClaims(token).get("tokenType", String.class);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }
}
