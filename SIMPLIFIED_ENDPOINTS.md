# PostulyTn - Simplified Endpoints (JWT Protected)

## ✅ All Endpoints Require JWT Token (Except Auth)

---

## 🔐 Authentication Endpoints (PUBLIC - No Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new recruiter |
| POST | `/auth/login` | Login and get JWT tokens |
| POST | `/auth/refresh` | Refresh access token |

**Total: 3 endpoints**

---

## 🏢 Company Endpoints (JWT Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/companies` | Create company |
| GET | `/companies` | Get all companies |
| GET | `/companies/{id}` | Get company by ID |
| DELETE | `/companies/{id}` | Delete company |

**Total: 4 endpoints**

---

## 👤 Recruiter Endpoints (JWT Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/recruiters` | Get all recruiters |
| GET | `/recruiters/{id}` | Get recruiter by ID |
| PUT | `/recruiters/{id}` | Update recruiter |
| DELETE | `/recruiters/{id}` | Delete recruiter |

**Total: 4 endpoints**

---

## 💼 Job Endpoints (JWT Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/jobs` | Create job posting |
| GET | `/jobs` | Get all jobs |
| GET | `/jobs/{id}` | Get job by ID |
| DELETE | `/jobs/{id}` | Delete job |

**Total: 4 endpoints**

---

## 📝 Application Endpoints (JWT Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/applications` | Submit application |
| GET | `/applications` | Get all applications |
| GET | `/applications/{id}` | Get application by ID |
| PUT | `/applications/{id}/status` | Update application status |
| DELETE | `/applications/{id}` | Delete application |

**Total: 5 endpoints**

---

## 📊 Summary

| Category | Endpoints | JWT Required |
|----------|-----------|--------------|
| **Authentication** | 3 | ❌ No |
| **Companies** | 4 | ✅ Yes |
| **Recruiters** | 4 | ✅ Yes |
| **Jobs** | 4 | ✅ Yes |
| **Applications** | 5 | ✅ Yes |
| **TOTAL** | **20** | **17 protected** |

**Removed endpoints:**
- ~~GET /companies/{id}/exists~~
- ~~GET /recruiters/email/{email}~~
- ~~GET /recruiters/company/{companyId}~~
- ~~GET /jobs/company/{companyId}~~
- ~~GET /jobs/{id}/exists~~
- ~~GET /applications/job/{jobId}~~

---

## 🔒 JWT Security Configuration

### Gateway Routes (ALL Protected)

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: false  # CRITICAL: Prevents unsecured automatic routes
      routes:
        - id: company-service
          uri: lb://COMPANY-SERVICE
          predicates:
            - Path=/companies/**,/recruiters/**,/auth/**
          filters:
            - JwtAuth
            
        - id: job-service
          uri: lb://JOB-SERVICE
          predicates:
            - Path=/jobs/**
          filters:
            - JwtAuth
            
        - id: application-service
          uri: lb://APPLICATION-SERVICE
          predicates:
            - Path=/applications/**
          filters:
            - JwtAuth
```

### Public Endpoints (No Token Required)

The `JwtAuthGatewayFilterFactory` automatically allows these endpoints without authentication:
- `/auth/login`
- `/auth/register`
- `/auth/refresh`
- `/actuator`

**All other endpoints REQUIRE valid JWT token in Authorization header**

---

## 🎯 Testing Flow

### 1. Register & Login
```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Ahmed","email":"ahmed@company.tn","password":"Pass123!","companyId":1,"role":"RECRUITER"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ahmed@company.tn","password":"Pass123!"}'

# Save token
export TOKEN="paste_your_token_here"
```

### 2. Test Protected Endpoints
```bash
# WITHOUT TOKEN - Should FAIL with 401
curl http://localhost:8080/companies

# WITH TOKEN - Should SUCCEED
curl http://localhost:8080/companies \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Complete CRUD Example
```bash
# Create Company
curl -X POST http://localhost:8080/companies \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Tech Co","description":"IT company","industry":"Technology","location":"Tunis","website":"https://tech.tn"}'

# Get All Companies
curl http://localhost:8080/companies \
  -H "Authorization: Bearer $TOKEN"

# Get Company by ID
curl http://localhost:8080/companies/1 \
  -H "Authorization: Bearer $TOKEN"

# Delete Company
curl -X DELETE http://localhost:8080/companies/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## ✅ Security Verification

### Test 1: No Token
```bash
curl http://localhost:8080/companies
```
**Expected:** `{"error": "Missing Authorization header"}` (401)

### Test 2: Invalid Token
```bash
curl http://localhost:8080/companies \
  -H "Authorization: Bearer invalid_token"
```
**Expected:** `{"error": "Invalid or expired token"}` (401)

### Test 3: Valid Token
```bash
curl http://localhost:8080/companies \
  -H "Authorization: Bearer $TOKEN"
```
**Expected:** List of companies (200)

---

## 🚀 Restart Instructions

After these changes, restart Gateway Service:

```bash
# Stop gateway (Ctrl+C in the terminal)
# Then restart:
cd /Users/monther/IdeaProjects/PostulyTn.Project
./mvnw spring-boot:run -pl gateway-service
```

Wait 30 seconds for service registration, then test!
