# PostulyTn Demo - Quick Reference Checklist

## 🚀 Pre-Demo Setup (5 minutes)

### Start Services in Order:
```bash
# Terminal 1 - Discovery Service
cd discovery-service
mvn spring-boot:run

# Terminal 2 - Config Service  
cd config-service
mvn spring-boot:run

# Terminal 3 - Gateway Service
cd gateway-service
mvn spring-boot:run

# Terminal 4 - Company Service
cd company-service
mvn spring-boot:run

# Terminal 5 - Job Service
cd job-service
mvn spring-boot:run

# Terminal 6 - Application Service
cd application-service
mvn spring-boot:run
```

### Verify Everything is Running:
- [ ] Eureka: http://localhost:8761 (all 4 services registered)
- [ ] MySQL: Running on port 3306
- [ ] RabbitMQ: http://localhost:15672 (guest/guest)

---

## 📋 Demo Flow (15-20 minutes)

### **STEP 1: Create Company** ✅
```bash
curl -X POST http://localhost:8080/companies \
  -H "Content-Type: application/json" \
  -d '{"name":"Tech Innovators","description":"Software company","industry":"Technology","location":"Tunis","website":"https://techinnovators.tn"}'
```
**Save Company ID: _____**

---

### **STEP 2: Register Recruiter** ✅
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Ahmed Ben Ali","email":"ahmed@techinnovators.tn","password":"SecurePass123!","companyId":1,"role":"RECRUITER"}'
```

---

### **STEP 3: Login & Get Token** ✅
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ahmed@techinnovators.tn","password":"SecurePass123!"}'
```
**Copy TOKEN and set:**
```bash
export TOKEN="paste_token_here"
```

---

### **STEP 4: Get All Companies** ✅
```bash
curl http://localhost:8080/companies -H "Authorization: Bearer $TOKEN"
```

---

### **STEP 5: Get All Recruiters** ✅
```bash
curl http://localhost:8080/recruiters -H "Authorization: Bearer $TOKEN"
```

---

### **STEP 6: Create Job** ✅
```bash
curl -X POST http://localhost:8080/jobs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Full Stack Developer","description":"Experienced developer needed","requirements":"Java, Spring Boot, React","location":"Tunis","salary":"3000-4500 TND","jobType":"FULL_TIME","companyId":1,"postedBy":1}'
```
**Save Job ID: _____**

---

### **STEP 7: Get All Jobs** ✅
```bash
curl http://localhost:8080/jobs -H "Authorization: Bearer $TOKEN"
```

---

### **STEP 8: Submit Application** ✅
```bash
curl -X POST http://localhost:8080/applications \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"jobId":1,"candidateName":"Sara Mohamed","candidateEmail":"sara@email.com","candidatePhone":"+216 20 123 456","resumeUrl":"https://example.com/resume.pdf","coverLetter":"I am interested in this position"}'
```
**Save Application ID: _____**

---

### **STEP 9: Get Applications for Job** ✅
```bash
curl http://localhost:8080/applications/job/1 -H "Authorization: Bearer $TOKEN"
```

---

### **STEP 10: Update Application Status** ✅
```bash
curl -X PUT http://localhost:8080/applications/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"REVIEWED"}'
```

---

### **STEP 11: Show Event-Driven Communication** ✅
**Open RabbitMQ Dashboard:** http://localhost:15672
- Show queues: `job.queue`, `application.queue`
- Show message counts
- Check service logs for event processing

---

### **STEP 12: Test Security** ✅
```bash
# This should FAIL (401 Unauthorized)
curl http://localhost:8080/jobs -H "Authorization: Bearer invalid_token"

# This should FAIL (401 Unauthorized)  
curl http://localhost:8080/jobs
```

---

### **STEP 13: Refresh Token** ✅
```bash
export REFRESH_TOKEN="paste_refresh_token_from_login"

curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

---

## 🎯 Key Points to Highlight

### Architecture:
- ✅ **5 Microservices** (Discovery, Config, Gateway, Company, Job, Application)
- ✅ **Service Discovery** with Eureka
- ✅ **API Gateway** for routing and security
- ✅ **Event-Driven** with RabbitMQ
- ✅ **JWT Authentication** with refresh tokens

### Features Demonstrated:
- ✅ User authentication & authorization
- ✅ CRUD operations on all entities
- ✅ Service-to-service communication via events
- ✅ Token-based security
- ✅ Microservices orchestration
- ✅ Error handling & validation

### Technologies:
- ✅ Spring Boot 3.2.5
- ✅ Spring Cloud (Eureka, Gateway, Config)
- ✅ MySQL Database
- ✅ RabbitMQ Message Broker
- ✅ JWT (jjwt)
- ✅ OpenAPI/Swagger
- ✅ Maven

---

## 📊 Swagger Documentation

Show API documentation for each service:
- Company: http://localhost:8081/swagger-ui.html
- Job: http://localhost:8082/swagger-ui.html  
- Application: http://localhost:8083/swagger-ui.html

---

## 🔍 Live Monitoring

### Eureka Dashboard
http://localhost:8761
- Show all registered services
- Health status
- Instances count

### RabbitMQ Management
http://localhost:15672 (guest/guest)
- Queues overview
- Message rates
- Connections

---

## ❌ Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Service not registered in Eureka | Wait 30 seconds, check logs |
| Port already in use | Kill process: `lsof -ti:PORT \| xargs kill -9` |
| Token expired | Use refresh token endpoint |
| 401 Unauthorized | Check token is set correctly |
| Events not received | Verify RabbitMQ is running |

---

## 🎬 Demo Script (What to Say)

### Introduction (2 min)
"This is PostulyTn, a microservices-based recruitment platform built with Spring Boot and Spring Cloud."

### Architecture Overview (3 min)
"We have 5 microservices:
- Discovery Service for service registration
- Gateway for routing and security  
- Company Service for managing companies and recruiters
- Job Service for job postings
- Application Service for candidate applications

All communication goes through the Gateway, which validates JWT tokens."

### Live Demo (10 min)
"Let me show you the complete flow:
1. First, I create a company
2. Then register a recruiter
3. Login to get a JWT token
4. Create a job posting - this publishes an event to RabbitMQ
5. Submit an application - this triggers another event
6. Update application status - the job service receives this event
7. All protected endpoints require the JWT token"

### Event-Driven Architecture (3 min)
"Notice in RabbitMQ, when a job is created, an event is published. The Application Service listens to this and can react. Similarly, when an application is submitted, the Job Service is notified. This loose coupling allows services to evolve independently."

### Security (2 min)
"All endpoints except login/register require authentication. The Gateway validates tokens and forwards user context to downstream services. Tokens expire in 24 hours, but we have refresh tokens valid for 7 days."

---

## ✅ Success Criteria

Your demo is successful if you can show:
- [x] All services running in Eureka
- [x] Complete CRUD flow for all entities
- [x] JWT authentication working
- [x] Events being published and consumed
- [x] Error handling (invalid token, missing auth)
- [x] Swagger documentation accessible

---

## 📝 Notes Section

**Questions Asked:**
- 
- 
- 

**Issues Encountered:**
- 
- 
- 

**Feedback Received:**
- 
- 
-
