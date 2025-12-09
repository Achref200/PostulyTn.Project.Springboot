# JWT Authentication Guide

## Overview

The PostulyTn microservices architecture implements a comprehensive JWT-based authentication system with token refresh capabilities. All authentication is handled through the **Company Service** and validated by the **Gateway Service**.

---

## Architecture

### Components

1. **Company Service (Port 8081)**
   - Handles user authentication (login/register)
   - Generates JWT access and refresh tokens
   - Token expiration: Access (24h), Refresh (7 days)

2. **Gateway Service (Port 8080)**
   - Validates JWT tokens for all routes
   - Extracts user context from tokens
   - Forwards user info as headers to downstream services

3. **Job Service & Application Service**
   - Read user context from forwarded headers
   - Use `UserContext` utility for authorization

---

## Authentication Endpoints

### 1. Register (Public)
```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "companyId": 1,
  "role": "RECRUITER"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "RECRUITER",
  "companyId": 1
}
```

### 2. Login (Public)
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "recruiterId": 1,
  "email": "john@example.com",
  "role": "RECRUITER",
  "companyId": 1
}
```

### 3. Refresh Token (Public)
```http
POST http://localhost:8080/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "recruiterId": 1,
  "email": "john@example.com",
  "role": "RECRUITER",
  "companyId": 1
}
```

---

## Making Authenticated Requests

### Example: Get All Jobs
```http
GET http://localhost:8080/jobs
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Example: Create Job
```http
POST http://localhost:8080/jobs
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "title": "Software Engineer",
  "description": "...",
  "companyId": 1
}
```

---

## JWT Token Structure

### Access Token Claims
```json
{
  "sub": "john@example.com",
  "role": "RECRUITER",
  "recruiterId": 1,
  "companyId": 1,
  "iat": 1702123456,
  "exp": 1702209856
}
```

### Refresh Token Claims
```json
{
  "sub": "john@example.com",
  "role": "RECRUITER",
  "recruiterId": 1,
  "companyId": 1,
  "tokenType": "refresh",
  "iat": 1702123456,
  "exp": 1702728256
}
```

---

## Using UserContext in Services

### In Job Service or Application Service Controllers

```java
import com.postulytn.job.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jobs")
public class JobController {
    
    @PostMapping
    public ResponseEntity<JobDTO> createJob(
            @RequestBody JobDTO jobDTO,
            HttpServletRequest request) {
        
        // Extract user information
        String email = UserContext.getEmail(request);
        String role = UserContext.getRole(request);
        Long recruiterId = UserContext.getRecruiterId(request);
        Long companyId = UserContext.getCompanyId(request);
        
        // Check authentication
        if (!UserContext.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Check role-based authorization
        if (!UserContext.hasRole(request, "RECRUITER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Use user context in business logic
        jobDTO.setRecruiterId(recruiterId);
        jobDTO.setCompanyId(companyId);
        
        return ResponseEntity.ok(jobService.createJob(jobDTO));
    }
}
```

---

## Gateway Filter Configuration

The JWT filter is automatically applied to all routes:
- ✅ `/companies/**` - Protected
- ✅ `/recruiters/**` - Protected
- ✅ `/jobs/**` - Protected
- ✅ `/applications/**` - Protected
- 🔓 `/auth/login` - Public
- 🔓 `/auth/register` - Public
- 🔓 `/auth/refresh` - Public

---

## User Context Headers

The gateway automatically forwards these headers to downstream services:

| Header | Description | Example |
|--------|-------------|---------|
| `X-User-Email` | User's email address | john@example.com |
| `X-User-Role` | User's role | RECRUITER |
| `X-User-RecruiterId` | Recruiter ID | 1 |
| `X-User-CompanyId` | Company ID | 1 |

---

## Configuration

### JWT Secret (All Services)
```yaml
jwt:
  secret: PostulyTnJoinSecretKey2025VeryLongSecretKeyForHS256Algorithm
```

### Token Expiration (Company Service)
```yaml
jwt:
  expiration: 86400000      # 24 hours
  refresh-expiration: 604800000  # 7 days
```

---

## Error Responses

### Invalid Token
```http
401 Unauthorized
Content-Type: application/json

{
  "error": "Invalid or expired token"
}
```

### Missing Authorization Header
```http
401 Unauthorized
Content-Type: application/json

{
  "error": "Missing Authorization header"
}
```

### Invalid Credentials
```http
400 Bad Request

{
  "message": "Invalid email or password"
}
```

---

## Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'
```

### Access Protected Endpoint
```bash
TOKEN="your_access_token_here"
curl http://localhost:8080/jobs \
  -H "Authorization: Bearer $TOKEN"
```

### Refresh Token
```bash
REFRESH_TOKEN="your_refresh_token_here"
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

---

## Security Best Practices

1. ✅ **Never hardcode JWT secret** - Use environment variables in production
2. ✅ **Use HTTPS** in production to prevent token interception
3. ✅ **Store tokens securely** on the client (HttpOnly cookies or secure storage)
4. ✅ **Implement token rotation** using refresh tokens
5. ✅ **Validate all requests** at the gateway level
6. ✅ **Use short-lived access tokens** (24 hours)
7. ✅ **Log authentication events** for security monitoring

---

## Token Lifecycle

```
1. User logs in → Receives access token (24h) + refresh token (7 days)
2. Use access token for API requests
3. Access token expires → Use refresh token to get new tokens
4. Refresh token expires → User must log in again
```

---

## Implementation Files

### Gateway Service
- `JwtUtil.java` - Token validation and claim extraction
- `JwtAuthenticationFilter.java` - Token validation filter
- `application.yml` - Route configuration

### Company Service
- `JwtUtil.java` - Token generation and validation
- `AuthController.java` - Authentication endpoints
- `AuthService.java` - Authentication logic

### Job/Application Services
- `UserContext.java` - Header extraction utility
