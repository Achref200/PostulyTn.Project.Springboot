# PostulyTn Microservices Demo Testing Guide

## 🚀 Quick Start

```bash
# 1. Build project with Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./mvnw clean install -DskipTests

# 2. Start all services
./run-services.sh

# 3. Verify services are up
open http://localhost:8761  # Eureka Dashboard

# 4. Test JWT authentication
./test-jwt-auth.sh

# 5. Ready for demo! 🎉
```

**Service URLs:**
- 🔍 Eureka Dashboard: http://localhost:8761
- ⚙️ Config Server: http://localhost:8888
- 🌐 API Gateway: http://localhost:8080
- 🏢 Company Service: http://localhost:8081 (+ Swagger: /swagger-ui.html)
- 💼 Job Service: http://localhost:8082 (+ Swagger: /swagger-ui.html)
- 📝 Application Service: http://localhost:8083 (+ Swagger: /swagger-ui.html)

---

## Pre-Demo Checklist

### ⚙️ Prerequisites

**IMPORTANT:** This project requires **Java 17**. Verify your Java version:
```bash
java -version
# Should show: openjdk version "17.x.x"
```

If using Java 25 or other version, switch to Java 17:
```bash
# macOS:
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Linux:
export JAVA_HOME=/path/to/java-17
```

### ✅ Start All Services

**Option 1: Using the Startup Script (Recommended)**
```bash
# From project root
./run-services.sh
```

This script will:
- ✅ Use Java 17 automatically
- ✅ Kill any existing services
- ✅ Start all services in the correct order
- ✅ Save logs to `./logs/` directory

**Option 2: Manual Start (in this order)**

1. **Discovery Service** (Port 8761) - Wait 10s
2. **Config Service** (Port 8888) - Wait 5s
3. **Gateway Service** (Port 8080) - Wait 5s
4. **Company Service** (Port 8081)
5. **Job Service** (Port 8082)
6. **Application Service** (Port 8083)

```bash
# Manual start example
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
cd discovery-service && java -jar target/*.jar &
sleep 10
cd ../config-service && java -jar target/*.jar &
# ... etc
```

### ✅ Verify Services are Running:

```bash
# Check all ports are listening
lsof -i :8761 -i :8888 -i :8080 -i :8081 -i :8082 -i :8083 | grep LISTEN

# Check Eureka Dashboard
open http://localhost:8761

# Verify all services are registered:
# - DISCOVERY-SERVICE
# - CONFIG-SERVICE
# - GATEWAY-SERVICE (may take 30-60s to appear)
# - COMPANY-SERVICE
# - JOB-SERVICE
# - APPLICATION-SERVICE
```

### ✅ Test JWT Authentication:

```bash
# Run the JWT authentication test script
./test-jwt-auth.sh
```

**Expected output:**
- ✅ `/companies/1` without token → 401 Unauthorized
- ✅ `/auth/login` without token → Accessible (public endpoint)
- ✅ Invalid token → 401 Unauthorized

If all tests pass, JWT authentication is working correctly!

---

## Demo Flow (Complete Testing Scenario)

### **Phase 1: Setup & Authentication**

#### Step 1: Create a Company

> **Note:** The `/auth/register` endpoint requires a company to exist first, but company creation is now protected. You have two options:
> 1. Temporarily disable JWT on company creation endpoint, OR
> 2. Pre-populate the database with a company

**For demo purposes, create company WITHOUT authentication (if needed):**
```bash
curl -X POST http://localhost:8081/companies \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tech Innovators Ltd",
    "description": "Leading software development company",
    "industry": "Technology",
    "location": "Tunis, Tunisia",
    "website": "https://techinnovators.tn"
  }'
# Note: Directly calling company-service on port 8081 (bypasses gateway)
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "name": "Tech Innovators Ltd",
  "description": "Leading software development company",
  "industry": "Technology",
  "location": "Tunis, Tunisia",
  "website": "https://techinnovators.tn"
}
```

**✅ Verify:** Company ID = 1 (save this for next steps)

---

#### Step 2: Register a Recruiter
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmed Ben Ali",
    "email": "ahmed@techinnovators.tn",
    "password": "SecurePass123!",
    "companyId": 1,
    "role": "RECRUITER"
  }'
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "name": "Ahmed Ben Ali",
  "email": "ahmed@techinnovators.tn",
  "role": "RECRUITER",
  "companyId": 1
}
```

**✅ Verify:** Recruiter registered successfully

---

#### Step 3: Login to Get JWT Token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmed@techinnovators.tn",
    "password": "SecurePass123!"
  }'
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUkVDUlVJVEVSIiwicmVjcnVpdGVySWQiOjEsImNvbXBhbnlJZCI6MSwic3ViIjoiYWhtZWRAdGVjaGlubm92YXRvcnMudG4iLCJpYXQiOjE3MDIxNDUyNzIsImV4cCI6MTcwMjIzMTY3Mn0.xyz...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "recruiterId": 1,
  "email": "ahmed@techinnovators.tn",
  "role": "RECRUITER",
  "companyId": 1
}
```

**✅ IMPORTANT:** Copy the `token` value and set it as an environment variable:
```bash
export TOKEN="paste_your_token_here"
```

---

### **Phase 2: Company Service Tests**

#### Step 4: Get All Companies
```bash
curl -X GET http://localhost:8080/companies \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Tech Innovators Ltd",
    "description": "Leading software development company",
    "industry": "Technology",
    "location": "Tunis, Tunisia",
    "website": "https://techinnovators.tn"
  }
]
```

**✅ Verify:** List contains the created company

---

#### Step 5: Get Company by ID
```bash
curl -X GET http://localhost:8080/companies/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):** Same company object

**✅ Verify:** Company details match

---

#### Step 6: Check if Company Exists
```bash
curl -X GET http://localhost:8080/companies/1/exists \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
true
```

**✅ Verify:** Returns `true`

---

### **Phase 3: Recruiter Service Tests**

#### Step 7: Get All Recruiters
```bash
curl -X GET http://localhost:8080/recruiters \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Ahmed Ben Ali",
    "email": "ahmed@techinnovators.tn",
    "role": "RECRUITER",
    "companyId": 1
  }
]
```

**✅ Verify:** Recruiter appears in the list

---

#### Step 8: Get Recruiter by ID
```bash
curl -X GET http://localhost:8080/recruiters/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):** Recruiter details

**✅ Verify:** Correct recruiter information

---

#### Step 9: Get Recruiter by Email
```bash
curl -X GET http://localhost:8080/recruiters/email/ahmed@techinnovators.tn \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):** Same recruiter details

**✅ Verify:** Email lookup works

---

#### Step 10: Get Recruiters by Company
```bash
curl -X GET http://localhost:8080/recruiters/company/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):** List of recruiters for company 1

**✅ Verify:** Contains Ahmed Ben Ali

---

### **Phase 4: Job Service Tests**

#### Step 11: Create a Job Posting
```bash
curl -X POST http://localhost:8080/jobs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Senior Full Stack Developer",
    "description": "We are looking for an experienced full-stack developer proficient in Java Spring Boot and React",
    "requirements": "5+ years experience, Java, Spring Boot, React, PostgreSQL",
    "location": "Tunis, Tunisia",
    "salary": "3000-4500 TND",
    "jobType": "FULL_TIME",
    "companyId": 1,
    "postedBy": 1
  }'
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "title": "Senior Full Stack Developer",
  "description": "We are looking for an experienced full-stack developer...",
  "requirements": "5+ years experience, Java, Spring Boot, React, PostgreSQL",
  "location": "Tunis, Tunisia",
  "salary": "3000-4500 TND",
  "jobType": "FULL_TIME",
  "status": "OPEN",
  "companyId": 1,
  "postedBy": 1,
  "createdAt": "2024-12-09T18:00:00"
}
```

**✅ Verify:** Job ID = 1, status = OPEN

---

#### Step 12: Get All Jobs
```bash
curl -X GET http://localhost:8080/jobs \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):** List with the created job

**✅ Verify:** Job appears in the list

---

#### Step 13: Get Jobs by Company
```bash
curl -X GET http://localhost:8080/jobs/company/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):** Jobs for company 1

**✅ Verify:** Contains the Full Stack Developer job

---

#### Step 14: Check if Job Exists
```bash
curl -X GET http://localhost:8080/jobs/1/exists \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
true
```

**✅ Verify:** Returns `true`

---

### **Phase 5: Application Service Tests**

#### Step 15: Submit a Job Application
```bash
curl -X POST http://localhost:8080/applications \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": 1,
    "candidateName": "Sara Mohamed",
    "candidateEmail": "sara.mohamed@email.com",
    "candidatePhone": "+216 20 123 456",
    "resumeUrl": "https://storage.example.com/resumes/sara-mohamed.pdf",
    "coverLetter": "I am very interested in the Full Stack Developer position..."
  }'
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "jobId": 1,
  "candidateName": "Sara Mohamed",
  "candidateEmail": "sara.mohamed@email.com",
  "candidatePhone": "+216 20 123 456",
  "resumeUrl": "https://storage.example.com/resumes/sara-mohamed.pdf",
  "coverLetter": "I am very interested in the Full Stack Developer position...",
  "status": "PENDING",
  "appliedAt": "2024-12-09T18:05:00"
}
```

**✅ Verify:** 
- Application ID = 1
- Status = PENDING
- Event published (check application logs for "ApplicationSubmittedEvent")

---

#### Step 16: Get All Applications
```bash
curl -X GET http://localhost:8080/applications \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):** List with submitted application

**✅ Verify:** Application appears in the list

---

#### Step 17: Get Applications by Job
```bash
curl -X GET http://localhost:8080/applications/job/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):** Applications for job 1

**✅ Verify:** Contains Sara's application

---

#### Step 18: Update Application Status
```bash
curl -X PUT http://localhost:8080/applications/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "REVIEWED"
  }'
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "jobId": 1,
  "candidateName": "Sara Mohamed",
  "candidateEmail": "sara.mohamed@email.com",
  "status": "REVIEWED",
  "appliedAt": "2024-12-09T18:05:00"
}
```

**✅ Verify:** 
- Status changed to REVIEWED
- Event published (check logs for "ApplicationStatusUpdatedEvent")

---

### **Phase 6: Event-Driven Communication Tests**

#### Step 19: Verify RabbitMQ Events

**Check RabbitMQ Management Console:**
```bash
open http://localhost:15672
# Login: guest / guest
```

**Expected Queues:**
- `job.queue` - Should have received JobCreatedEvent
- `application.queue` - Should have events

**✅ Verify in Logs:**
```bash
# Check Application Service logs
# Should see: "Received JobCreatedEvent for job: 1"

# Check Job Service logs  
# Should see: "Received ApplicationSubmittedEvent for job: 1"
# Should see: "Received ApplicationStatusUpdatedEvent..."
```

---

### **Phase 7: Additional Scenarios**

#### Step 20: Test Token Refresh
```bash
# Save refresh token from login response
export REFRESH_TOKEN="your_refresh_token_here"

curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$REFRESH_TOKEN\"
  }"
```

**Expected Response (200 OK):** New token pair

**✅ Verify:** New access token received

---

#### Step 21: Create Another Job (Diversity Test)
```bash
curl -X POST http://localhost:8080/jobs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "DevOps Engineer",
    "description": "Looking for DevOps engineer to manage our cloud infrastructure",
    "requirements": "AWS, Docker, Kubernetes, CI/CD",
    "location": "Remote",
    "salary": "2500-3500 TND",
    "jobType": "REMOTE",
    "companyId": 1,
    "postedBy": 1
  }'
```

**Expected Response (201 Created):** Job ID = 2

**✅ Verify:** Second job created successfully

---

#### Step 22: Submit Multiple Applications
```bash
# Application 2
curl -X POST http://localhost:8080/applications \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": 2,
    "candidateName": "Mohamed Salah",
    "candidateEmail": "mohamed.salah@email.com",
    "candidatePhone": "+216 22 987 654",
    "resumeUrl": "https://storage.example.com/resumes/mohamed-salah.pdf",
    "coverLetter": "Experienced DevOps engineer with AWS certification..."
  }'
```

**Expected Response (201 Created):** Application ID = 2

**✅ Verify:** Multiple applications working

---

### **Phase 8: Delete Operations**

#### Step 23: Delete an Application
```bash
curl -X DELETE http://localhost:8080/applications/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (204 No Content)**

**✅ Verify:** Application deleted

---

#### Step 24: Delete a Job
```bash
curl -X DELETE http://localhost:8080/jobs/2 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (204 No Content)**

**✅ Verify:** Job deleted

---

#### Step 25: Delete a Company
```bash
# Note: This should fail if there are associated recruiters/jobs
curl -X DELETE http://localhost:8080/companies/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:** May return error if constraints exist

---

## **Phase 9: Error Handling Tests**

#### Test Invalid Token
```bash
curl -X GET http://localhost:8080/jobs \
  -H "Authorization: Bearer invalid_token"
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Invalid or expired token"
}
```

✅ **Verify:** Gateway properly rejects invalid tokens

---

#### Test Missing Authorization
```bash
curl -X GET http://localhost:8080/jobs
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Missing Authorization header"
}
```

✅ **Verify:** Protected endpoints require authentication

---

#### Test Public Endpoints (No Token Required)
```bash
# These should work WITHOUT authentication:
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test"}'

curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","password":"test123","companyId":1,"role":"RECRUITER"}'
```

**Expected:** These endpoints are accessible without JWT token

✅ **Verify:** Public endpoints bypass JWT authentication

---

#### Test Non-Existent Resource
```bash
curl -X GET http://localhost:8080/companies/999 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (404 Not Found)**

---

## Demo Presentation Checklist

### ✅ Before Demo:
- [ ] **Java 17** is active (verify with `java -version`)
- [ ] All services built successfully (`./mvnw clean install -DskipTests`)
- [ ] MySQL database is running
- [ ] RabbitMQ is running (`docker ps` or check port 5672)
- [ ] Start all services (`./run-services.sh`)
- [ ] All services registered in Eureka (check http://localhost:8761)
- [ ] JWT authentication working (`./test-jwt-auth.sh` passes)
- [ ] Check all service logs for errors (`ls -la logs/`)
- [ ] Prepare Postman collection (optional but recommended)

### ✅ During Demo Show:
1. **Eureka Dashboard** - All services registered
2. **Authentication Flow** - Register → Login → Get Token
3. **CRUD Operations** - Create, Read, Update, Delete
4. **Microservices Communication** - Event-driven architecture
5. **Security** - JWT token validation
6. **Error Handling** - Invalid requests, missing auth
7. **Swagger UI** - API documentation for each service

### ✅ Swagger UI Links:
- Company Service: http://localhost:8081/swagger-ui.html
- Job Service: http://localhost:8082/swagger-ui.html
- Application Service: http://localhost:8083/swagger-ui.html

---

## Quick Test Summary

| Phase | Service | Endpoints Tested | Status |
|-------|---------|------------------|--------|
| 1 | Auth | Register, Login, Refresh | ✅ |
| 2 | Company | Create, Get All, Get by ID, Exists | ✅ |
| 3 | Recruiter | Get All, Get by ID, Get by Email, Get by Company | ✅ |
| 4 | Job | Create, Get All, Get by Company, Exists | ✅ |
| 5 | Application | Create, Get All, Get by Job, Update Status | ✅ |
| 6 | Events | RabbitMQ message passing | ✅ |
| 7 | Security | Token validation, Error handling | ✅ |

---

## Troubleshooting

### ❌ Error: "Could not find or load main class"
**Cause:** Services not compiled or wrong Java version

**Solution:**
```bash
# Ensure Java 17 is active
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
java -version  # Verify it shows 17.x.x

# Rebuild all services
./mvnw clean install -DskipTests
```

### ❌ Gateway Service fails to start
**Cause:** Discovery or Config service not running

**Solution:**
```bash
# Services must start in order:
# 1. Discovery (8761) - wait 10s
# 2. Config (8888) - wait 5s  
# 3. Gateway (8080)

# Or use the startup script:
./run-services.sh
```

### ❌ JWT Authentication Not Working (200 OK without token)
**Cause:** Gateway filter name mismatch (fixed in latest version)

**Verify Fix:**
```bash
# Check gateway configuration
grep "JwtAuth" gateway-service/src/main/resources/application.yml
# Should show "- JwtAuth" (NOT "- JwtAuthentication")

# Test authentication
./test-jwt-auth.sh
```

### ❌ If a service doesn't respond:
```bash
# Check if service is registered
curl http://localhost:8761/eureka/apps

# Check service health
curl http://localhost:8080/actuator/health

# Check service logs
tail -f logs/gateway-service.log
```

### ❌ If authentication fails:
- Ensure you copied the complete token (it's very long!)
- Check token hasn't expired (24 hours default)
- Use refresh token endpoint if expired
- Verify token format: `Authorization: Bearer <token>` (with space after "Bearer")

### ❌ If events aren't processed:
- Check RabbitMQ is running: `http://localhost:15672` (guest/guest)
- Check application logs for event listeners
- Verify queue bindings in RabbitMQ Management UI

### 🛑 Stop All Services
```bash
# Kill all running services
pkill -f 'java -jar'

# Verify all stopped
lsof -i :8761 -i :8888 -i :8080 -i :8081 -i :8082 -i :8083
```

### 📋 View Service Logs
```bash
# If using run-services.sh, logs are in:
tail -f logs/gateway-service.log
tail -f logs/company-service.log
tail -f logs/job-service.log
tail -f logs/application-service.log
```

---

## Success Criteria

Your demo is successful if:
- ✅ All 6 microservices are running (Discovery, Config, Gateway, Company, Job, Application)
- ✅ Can authenticate and receive JWT token
- ✅ Can perform CRUD operations on all entities
- ✅ Events are published and consumed correctly
- ✅ Security (JWT) works on protected endpoints
- ✅ Error handling returns proper HTTP status codes
- ✅ Swagger documentation is accessible

---

## 🛠️ Helper Scripts

The project includes these utility scripts:

### `run-services.sh`
Automated startup script that:
- Sets Java 17 environment
- Kills any existing services
- Starts all services in correct order with proper delays
- Redirects logs to `./logs/` directory

```bash
./run-services.sh
```

### `test-jwt-auth.sh`
JWT authentication validation script that:
- Tests protected endpoints without token (should fail with 401)
- Tests public endpoints without token (should succeed)
- Tests protected endpoints with invalid token (should fail with 401)

```bash
./test-jwt-auth.sh
```

**Output shows pass/fail for each test case.**

---

## 📝 Important Notes

### JWT Authentication
- **Filter name fixed:** Gateway uses `JwtAuth` (matches `JwtAuthGatewayFilterFactory`)
- **Public endpoints:** `/auth/login`, `/auth/register`, `/auth/refresh`, `/actuator/*`
- **Protected endpoints:** All others require `Authorization: Bearer <token>`

### Service Dependencies
```
Discovery (8761)
    ↓
Config (8888)
    ↓
Gateway (8080)
    ↓
Business Services (8081, 8082, 8083)
```

**Always start in this order!**

### Token Expiration
- **Access Token:** 24 hours
- **Refresh Token:** 7 days
- Use `/auth/refresh` endpoint to get new access token without re-login

---

**Good luck with your demo! 🚀**
