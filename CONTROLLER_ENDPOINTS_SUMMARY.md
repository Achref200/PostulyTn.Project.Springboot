# PostulyTn - Complete Controller Endpoints Summary

## Gateway Service (Port 8080) - Main Entry Point

All requests go through: `http://localhost:8080`

---

## 🔐 Authentication Controller (Company Service)

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/auth/register` | ❌ No | Register a new recruiter |
| POST | `/auth/login` | ❌ No | Login and get JWT tokens |
| POST | `/auth/refresh` | ❌ No | Refresh access token |

### Request Examples:

**Register:**
```json
POST /auth/register
{
  "name": "Ahmed Ben Ali",
  "email": "ahmed@company.com",
  "password": "SecurePass123!",
  "companyId": 1,
  "role": "RECRUITER"
}
```

**Login:**
```json
POST /auth/login
{
  "email": "ahmed@company.com",
  "password": "SecurePass123!"
}
```

**Refresh:**
```json
POST /auth/refresh
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## 🏢 Company Controller (Company Service)

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/companies` | ✅ Yes | Create a new company |
| GET | `/companies` | ✅ Yes | Get all companies |
| GET | `/companies/{id}` | ✅ Yes | Get company by ID |
| GET | `/companies/{id}/exists` | ✅ Yes | Check if company exists |
| DELETE | `/companies/{id}` | ✅ Yes | Delete a company |

### Request Examples:

**Create Company:**
```json
POST /companies
Authorization: Bearer {token}
{
  "name": "Tech Innovators Ltd",
  "description": "Leading software development company",
  "industry": "Technology",
  "location": "Tunis, Tunisia",
  "website": "https://techinnovators.tn"
}
```

**Response:**
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

---

## 👤 Recruiter Controller (Company Service)

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| GET | `/recruiters` | ✅ Yes | Get all recruiters |
| GET | `/recruiters/{id}` | ✅ Yes | Get recruiter by ID |
| GET | `/recruiters/email/{email}` | ✅ Yes | Get recruiter by email |
| GET | `/recruiters/company/{companyId}` | ✅ Yes | Get recruiters by company |
| PUT | `/recruiters/{id}` | ✅ Yes | Update recruiter |
| DELETE | `/recruiters/{id}` | ✅ Yes | Delete recruiter |

### Request Examples:

**Get Recruiter by Email:**
```bash
GET /recruiters/email/ahmed@company.com
Authorization: Bearer {token}
```

**Update Recruiter:**
```json
PUT /recruiters/1
Authorization: Bearer {token}
{
  "name": "Ahmed Ben Ali (Updated)",
  "email": "ahmed@company.com",
  "role": "RECRUITER",
  "companyId": 1
}
```

---

## 💼 Job Controller (Job Service)

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/jobs` | ✅ Yes | Create a new job posting |
| GET | `/jobs` | ✅ Yes | Get all jobs |
| GET | `/jobs/company/{companyId}` | ✅ Yes | Get jobs by company |
| GET | `/jobs/{id}/exists` | ✅ Yes | Check if job exists |
| DELETE | `/jobs/{id}` | ✅ Yes | Delete a job |

### Request Examples:

**Create Job:**
```json
POST /jobs
Authorization: Bearer {token}
{
  "title": "Senior Full Stack Developer",
  "description": "We are looking for an experienced full-stack developer",
  "requirements": "5+ years experience, Java, Spring Boot, React",
  "location": "Tunis, Tunisia",
  "salary": "3000-4500 TND",
  "jobType": "FULL_TIME",
  "companyId": 1,
  "postedBy": 1
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Senior Full Stack Developer",
  "description": "We are looking for an experienced full-stack developer",
  "requirements": "5+ years experience, Java, Spring Boot, React",
  "location": "Tunis, Tunisia",
  "salary": "3000-4500 TND",
  "jobType": "FULL_TIME",
  "status": "OPEN",
  "companyId": 1,
  "postedBy": 1,
  "createdAt": "2024-12-09T18:00:00"
}
```

**Job Types:** `FULL_TIME`, `PART_TIME`, `CONTRACT`, `REMOTE`, `INTERNSHIP`

---

## 📝 Application Controller (Application Service)

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/applications` | ✅ Yes | Submit a job application |
| GET | `/applications` | ✅ Yes | Get all applications |
| GET | `/applications/job/{jobId}` | ✅ Yes | Get applications by job |
| PUT | `/applications/{id}/status` | ✅ Yes | Update application status |
| DELETE | `/applications/{id}` | ✅ Yes | Delete an application |

### Request Examples:

**Submit Application:**
```json
POST /applications
Authorization: Bearer {token}
{
  "jobId": 1,
  "candidateName": "Sara Mohamed",
  "candidateEmail": "sara.mohamed@email.com",
  "candidatePhone": "+216 20 123 456",
  "resumeUrl": "https://storage.example.com/resumes/sara-mohamed.pdf",
  "coverLetter": "I am very interested in this position..."
}
```

**Response:**
```json
{
  "id": 1,
  "jobId": 1,
  "candidateName": "Sara Mohamed",
  "candidateEmail": "sara.mohamed@email.com",
  "candidatePhone": "+216 20 123 456",
  "resumeUrl": "https://storage.example.com/resumes/sara-mohamed.pdf",
  "coverLetter": "I am very interested in this position...",
  "status": "PENDING",
  "appliedAt": "2024-12-09T18:05:00"
}
```

**Update Status:**
```json
PUT /applications/1/status
Authorization: Bearer {token}
{
  "status": "REVIEWED"
}
```

**Application Statuses:** `PENDING`, `REVIEWED`, `ACCEPTED`, `REJECTED`

---

## 📊 Complete Endpoint Count

| Service | Total Endpoints | Public | Protected |
|---------|----------------|--------|-----------|
| **Auth** | 3 | 3 | 0 |
| **Companies** | 5 | 0 | 5 |
| **Recruiters** | 6 | 0 | 6 |
| **Jobs** | 5 | 0 | 5 |
| **Applications** | 5 | 0 | 5 |
| **TOTAL** | **24** | **3** | **21** |

---

## 🔄 Event-Driven Communication

### Events Published:

| Service | Event | When | Consumed By |
|---------|-------|------|-------------|
| Job Service | `JobCreatedEvent` | When job is created | Application Service |
| Job Service | `JobDeletedEvent` | When job is deleted | Application Service |
| Application Service | `ApplicationSubmittedEvent` | When application submitted | Job Service |
| Application Service | `ApplicationStatusUpdatedEvent` | When status changed | Job Service |

### Event Flow Example:

```
1. POST /jobs → JobCreatedEvent → RabbitMQ → Application Service
2. POST /applications → ApplicationSubmittedEvent → RabbitMQ → Job Service
3. PUT /applications/1/status → ApplicationStatusUpdatedEvent → RabbitMQ → Job Service
```

---

## 🔐 Authorization Header Format

All protected endpoints require:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUkVDUlVJVEVSIiwicmVjcnVpdGVySWQiOjEsImNvbXBhbnlJZCI6MSwic3ViIjoiYWhtZWRAdGVjaGlubm92YXRvcnMudG4iLCJpYXQiOjE3MDIxNDUyNzIsImV4cCI6MTcwMjIzMTY3Mn0.xyz...
```

---

## 📋 Testing Order for Demo

### Recommended Flow:

1. **Setup:** Create Company
2. **Auth:** Register Recruiter → Login → Get Token
3. **Read Operations:**
   - GET /companies
   - GET /recruiters
4. **Create Job:** POST /jobs
5. **Read Jobs:** GET /jobs
6. **Submit Application:** POST /applications
7. **Read Applications:** GET /applications/job/{jobId}
8. **Update Status:** PUT /applications/{id}/status
9. **Show Events:** Check RabbitMQ dashboard
10. **Test Security:** Try invalid token

---

## 🎯 Quick Test Commands

### Set Token Variable:
```bash
export TOKEN="your_token_here"
```

### Test All Endpoints:
```bash
# Companies
curl http://localhost:8080/companies -H "Authorization: Bearer $TOKEN"

# Recruiters
curl http://localhost:8080/recruiters -H "Authorization: Bearer $TOKEN"

# Jobs
curl http://localhost:8080/jobs -H "Authorization: Bearer $TOKEN"

# Applications
curl http://localhost:8080/applications -H "Authorization: Bearer $TOKEN"
```

---

## 🚨 Common HTTP Status Codes

| Code | Meaning | When You'll See It |
|------|---------|-------------------|
| 200 | OK | Successful GET, PUT requests |
| 201 | Created | Successful POST requests |
| 204 | No Content | Successful DELETE requests |
| 400 | Bad Request | Invalid request body/validation errors |
| 401 | Unauthorized | Missing/invalid token |
| 403 | Forbidden | Valid token but insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Internal server error |

---

## 📱 Swagger UI Access

Direct service access (when not going through gateway):

- **Company Service:** http://localhost:8081/swagger-ui.html
- **Job Service:** http://localhost:8082/swagger-ui.html
- **Application Service:** http://localhost:8083/swagger-ui.html

---

## ✅ Validation Rules

### Email Format:
- Must be valid email format
- Example: `user@domain.com`

### Password Requirements:
- Minimum length (typically 8+ characters)
- Required for registration

### Required Fields:

**Company:**
- name, industry, location

**Recruiter:**
- name, email, password

**Job:**
- title, description, companyId, postedBy

**Application:**
- jobId, candidateName, candidateEmail

---

## 🎓 Tips for Demo Success

1. ✅ Start all services in correct order
2. ✅ Verify Eureka shows all services
3. ✅ Save the JWT token immediately after login
4. ✅ Keep company ID and job ID handy
5. ✅ Have RabbitMQ dashboard open in browser
6. ✅ Test one happy path completely before demo
7. ✅ Have backup Postman collection ready
8. ✅ Check logs are clean (no errors)

---

**Total Microservices:** 6 (Discovery, Config, Gateway, Company, Job, Application)  
**Total Controllers:** 5 (Auth, Company, Recruiter, Job, Application)  
**Total Endpoints:** 24  
**Technologies:** Spring Boot, Spring Cloud, JWT, RabbitMQ, MySQL
