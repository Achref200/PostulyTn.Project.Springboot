package com.postulytn.application.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserContext {
    
    private static final String HEADER_EMAIL = "X-User-Email";
    private static final String HEADER_ROLE = "X-User-Role";
    private static final String HEADER_RECRUITER_ID = "X-User-RecruiterId";
    private static final String HEADER_COMPANY_ID = "X-User-CompanyId";
    
    public static String getEmail(HttpServletRequest request) {
        String email = request.getHeader(HEADER_EMAIL);
        return (email != null && !email.isEmpty()) ? email : null;
    }
    
    public static String getRole(HttpServletRequest request) {
        String role = request.getHeader(HEADER_ROLE);
        return (role != null && !role.isEmpty()) ? role : null;
    }
    
    public static Long getRecruiterId(HttpServletRequest request) {
        String recruiterId = request.getHeader(HEADER_RECRUITER_ID);
        if (recruiterId != null && !recruiterId.isEmpty()) {
            try {
                return Long.parseLong(recruiterId);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public static Long getCompanyId(HttpServletRequest request) {
        String companyId = request.getHeader(HEADER_COMPANY_ID);
        if (companyId != null && !companyId.isEmpty()) {
            try {
                return Long.parseLong(companyId);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public static boolean isAuthenticated(HttpServletRequest request) {
        return getEmail(request) != null;
    }
    
    public static boolean hasRole(HttpServletRequest request, String requiredRole) {
        String userRole = getRole(request);
        return userRole != null && userRole.equalsIgnoreCase(requiredRole);
    }
}
