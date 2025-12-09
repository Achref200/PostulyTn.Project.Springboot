# PostulyTN - Microservices Recruitment Platform

Spring Boot microservices application demonstrating synchronous and asynchronous communication patterns.

---

## Architecture Overview

```
                                    ┌─────────────────┐
                                    │  Discovery      │
                                    │  Service        │
                                    │  (Eureka:8761)  │
                                    └────────┬────────┘
                                             │ registers
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
           ┌────────▼────────┐     ┌────────▼────────┐     ┌────────▼────────┐
           │  Company        │     │  Job            │     │  Application    │
           │  Service:8081   │     │  Service:8082   │     │  Service:8083   │
           └────────┬────────┘     └────────┬────────┘     └────────┬────────┘
                    │                       │                        │
                    │    ┌──────────────────┼────────────────────────┤
                    │    │                  │                        │
                    ▼    ▼                  ▼                        ▼
           ┌─────────────────────────────────────────────────────────────────┐
           │                         RabbitMQ                                │
           │  ┌──────────────────┐  ┌──────────────────┐  ┌───────────────┐  │
           │  │ company.exchange │  │   job.exchange   │  │ application.  │  │
           │  │                  │  │                  │  │   exchange    │  │
           │  └──────────────────┘  └──────────────────┘  └───────────────┘  │
           └─────────────────────────────────────────────────────────────────┘
```

### Services

| Service | Port | Role |
|---------|------|------|
| Discovery Service | 8761 | Eureka Server |
| Gateway Service | 8080 | API Gateway |
| Company Service | 8081 | Company management + Event Publisher |
| Job Service | 8082 | Job management + Event Publisher/Listener |
| Application Service | 8083 | Application management + Event Publisher/Listener |

---

## Communication Patterns

### 1. Synchronous (REST + Feign)

```
Application-Service ──HTTP──► Job-Service ──HTTP──► Company-Service
        │                           │
        │ POST /applications        │ POST /jobs
        │ validates jobId exists    │ validates companyId exists
        ▼                           ▼
   JobClient.existsById()    CompanyClient.existsById()
```

### 2. Asynchronous (RabbitMQ Events)

```
Company-Service                Job-Service                Application-Service
      │                              │                            │
      │ COMPANY_CREATED             │ JOB_CREATED                │ APPLICATION_SUBMITTED
      │ COMPANY_UPDATED             │ JOB_PUBLISHED              │ APPLICATION_STATUS_CHANGED
      │ COMPANY_DELETED             │ JOB_CLOSED                 │
      │                              │                            │
      ▼                              ▼                            ▼
┌─────────────┐              ┌─────────────┐              ┌─────────────┐
│ company.    │              │ job.        │              │ application.│
│ exchange    │              │ exchange    │              │ exchange    │
└──────┬──────┘              └──────┬──────┘              └─────────────┘
       │                            │
       │ company.*                  │ job.*
       ▼                            ▼
 Job-Service                 Application-Service
 (CompanyEventListener)      (JobEventListener)
```

---

## Key Concepts for Exam

### CAP Theorem
- **Consistency**: All nodes see the same data
- **Availability**: System responds to requests
- **Partition Tolerance**: System works despite network failures
- This system uses **AP** (Availability + Partition Tolerance) with eventual consistency

### Event-Driven Architecture (EDA)
- Services publish events when state changes
- Other services listen and react asynchronously
- Decouples services (publisher doesn't know subscribers)
- Enables eventual consistency

### RabbitMQ Components
- **Exchange**: Routes messages to queues (TopicExchange)
- **Queue**: Stores messages until consumed
- **Binding**: Links exchange to queue with routing key
- **Routing Key**: Pattern for message routing (e.g., `job.created`, `company.*`)

### Feign Client
- Declarative REST client
- Interface-based (just annotations)
- Integrates with Eureka for service discovery
- Handles load balancing automatically  

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- IntelliJ IDEA (recommended)

## Database Setup

Create the following MySQL databases:
```sql
CREATE DATABASE db_company;
CREATE DATABASE db_job;
CREATE DATABASE db_application;
```

Update credentials in `config-service/src/main/resources/configurations/*.yml` if needed (default: root/root).

## Startup Sequence

**Important**: Services must be started in this order:

1. **Discovery Service** (Eureka Server)
```bash
cd discovery-service
mvn spring-boot:run
```
Access Eureka Dashboard: http://localhost:8761

2. **Config Service**
```bash
cd config-service
mvn spring-boot:run
```

3. **Domain Services** (can be started in parallel after Config is ready)
```bash
# Terminal 1
cd company-service
mvn spring-boot:run

# Terminal 2
cd job-service
mvn spring-boot:run

# Terminal 3
cd application-service
mvn spring-boot:run
```

4. **Gateway Service**
```bash
cd gateway-service
mvn spring-boot:run
```

## API Endpoints

### Companies (Port 8081)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /companies | Create company (publishes COMPANY_CREATED event) |
| GET | /companies | List all companies |
| GET | /companies/{id} | Get company by ID |
| DELETE | /companies/{id} | Delete (publishes COMPANY_DELETED event) |
| GET | /companies/{id}/exists | Check if company exists (used by Job Service) |

### Jobs (Port 8082)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /jobs | Create job (calls Company Service, publishes JOB_CREATED) |
| GET | /jobs | List all jobs |
| GET | /jobs/company/{companyId} | Get jobs by company |
| DELETE | /jobs/{id} | Delete (publishes JOB_CLOSED event) |
| GET | /jobs/{id}/exists | Check if job exists (used by Application Service) |

### Applications (Port 8083)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /applications | Create (calls Job Service, publishes APPLICATION_SUBMITTED) |
| GET | /applications | List all applications |
| GET | /applications/job/{jobId} | Get applications by job |
| PUT | /applications/{id}/status | Update status (publishes APPLICATION_STATUS_CHANGED) |
| DELETE | /applications/{id} | Delete application |

## Testing with Postman

### 1. Register a Recruiter
```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "name": "Ahmed Ben Ali",
  "email": "ahmed@company.tn",
  "password": "password123",
  "role": "ADMIN"
}
```

### 2. Login
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "ahmed@company.tn",
  "password": "password123"
}
```
Response contains JWT token.

### 3. Create Company
```http
POST http://localhost:8080/companies
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Tech Tunisia",
  "sector": "IT Services",
  "description": "Leading tech company in Tunisia",
  "location": "Sousse, Tunisia",
  "website": "https://techtn.com"
}
```

### 4. Create Job
```http
POST http://localhost:8080/jobs
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "title": "Java Developer",
  "description": "Spring Boot developer position",
  "role": "Backend Developer",
  "level": "MID",
  "contractType": "FULL_TIME",
  "location": "Sousse",
  "status": "PUBLISHED",
  "companyId": 1
}
```

### 5. Submit Application
```http
POST http://localhost:8080/applications
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "candidateId": 100,
  "cvUrl": "https://example.com/cv.pdf",
  "notes": "Experienced Java developer",
  "jobId": 1
}
```

## Project Structure

```
postuly-tn-join/
├── pom.xml                      # Parent POM
├── discovery-service/           # Eureka Server
├── config-service/              # Config Server + configurations
│   └── src/main/resources/configurations/
├── gateway-service/             # API Gateway + JWT Filter
├── company-service/             # Company & Recruiter management
│   ├── entity/                  # Company, Recruiter, Role
│   ├── dto/                     # DTOs for API responses
│   ├── mapper/                  # Entity ↔ DTO conversion
│   ├── repository/              # JPA repositories
│   ├── service/                 # Business logic
│   ├── controller/              # REST controllers
│   └── security/                # JWT & Security config
├── job-service/                 # Job management
│   ├── entity/                  # Job, Level, ContractType, JobStatus
│   ├── model/                   # Company (transient)
│   ├── client/                  # Feign Client for Company Service
│   └── ...
└── application-service/         # Application management
    ├── entity/                  # JobApplication, ApplicationStatus
    ├── model/                   # Job (transient)
    ├── client/                  # Feign Client for Job Service
    └── ...
```

---

## Code Examples for Exam

### 1. Feign Client (Synchronous Call)
```java
@FeignClient(name = "COMPANY-SERVICE")
public interface CompanyClient {
    @GetMapping("/companies/{id}/exists")
    Boolean existsById(@PathVariable Long id);
}
```

### 2. RabbitMQ Config
```java
@Configuration
public class RabbitMQConfig {
    public static final String JOB_EXCHANGE = "job.exchange";
    public static final String JOB_CREATED_QUEUE = "job.created.queue";
    public static final String JOB_CREATED_KEY = "job.created";

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(JOB_EXCHANGE);
    }

    @Bean
    public Queue jobCreatedQueue() {
        return QueueBuilder.durable(JOB_CREATED_QUEUE).build();
    }

    @Bean
    public Binding jobCreatedBinding(Queue jobCreatedQueue, TopicExchange jobExchange) {
        return BindingBuilder.bind(jobCreatedQueue).to(jobExchange).with(JOB_CREATED_KEY);
    }
}
```

### 3. Event Publisher
```java
@Component
@RequiredArgsConstructor
public class JobEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishJobCreated(Long jobId, String title, Long companyId) {
        JobEvent event = JobEvent.builder()
                .jobId(jobId)
                .title(title)
                .companyId(companyId)
                .eventType(JobEvent.EventType.CREATED)
                .timestamp(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend(JOB_EXCHANGE, JOB_CREATED_KEY, event);
    }
}
```

### 4. Event Listener
```java
@Component
public class JobEventListener {
    @RabbitListener(queues = RabbitMQConfig.JOB_EVENTS_QUEUE)
    public void handleJobEvent(JobEvent event) {
        switch (event.getEventType()) {
            case CREATED -> log.info("Job created: {}", event.getJobId());
            case CLOSED -> log.warn("Job closed: {}", event.getJobId());
        }
    }
}
```

---

## Exam Q&A

**Q: What is the difference between synchronous and asynchronous communication?**
- **Synchronous**: Service waits for response (REST/Feign). Used for validation.
- **Asynchronous**: Fire-and-forget (RabbitMQ). Used for notifications/events.

**Q: Why use RabbitMQ?**
- Decouples services
- Handles failures (messages persist in queue)
- Enables eventual consistency
- Services can be added/removed without changing others

**Q: What is a Topic Exchange?**
- Routes messages based on routing key patterns
- Uses wildcards: `*` (one word), `#` (zero or more words)
- Example: `job.*` matches `job.created`, `job.closed`

**Q: What is Eureka?**
- Service Discovery server
- Services register themselves on startup
- Clients lookup services by name instead of hardcoded URLs

**Q: What is eventual consistency?**
- Data may be temporarily inconsistent across services
- Eventually all services converge to same state
- Trade-off for availability in distributed systems

---

## Prerequisites

- Java 17
- MySQL 8.0+ (running on localhost:3306)
- RabbitMQ 3.x (running on localhost:5672)
- Maven 3.8+

## Run

```bash
# 1. Start MySQL and RabbitMQ services first

# 2. Build all modules
./mvnw clean install -DskipTests

# 3. Start services in separate terminals (in order)
./mvnw spring-boot:run -pl discovery-service
./mvnw spring-boot:run -pl company-service
./mvnw spring-boot:run -pl job-service
./mvnw spring-boot:run -pl application-service
./mvnw spring-boot:run -pl gateway-service
```

## Service URLs
- Eureka Dashboard: http://localhost:8761
- Gateway: http://localhost:8080
- RabbitMQ Management: http://localhost:15672 (guest/guest)

---

## 📚 Demo & Documentation

### Complete Demo Guides

1. **[DEMO_TESTING_GUIDE.md](./DEMO_TESTING_GUIDE.md)**
   - Complete step-by-step testing guide (25 steps)
   - Covers all controllers and endpoints
   - Includes expected responses and verification steps
   - Perfect for comprehensive testing

2. **[DEMO_QUICK_CHECKLIST.md](./DEMO_QUICK_CHECKLIST.md)**
   - Quick reference checklist for live demos
   - 13 essential steps to showcase all features
   - What to say during demo presentation
   - Troubleshooting common issues

3. **[CONTROLLER_ENDPOINTS_SUMMARY.md](./CONTROLLER_ENDPOINTS_SUMMARY.md)**
   - Complete API reference for all 24 endpoints
   - Request/response examples
   - HTTP status codes reference
   - Testing order recommendations

4. **[JWT_AUTHENTICATION_GUIDE.md](./JWT_AUTHENTICATION_GUIDE.md)**
   - JWT implementation details
   - Token structure and claims
   - Security best practices
   - UserContext usage examples

### Postman Collection

Import **[PostulyTn_Demo_Collection.postman_collection.json](./PostulyTn_Demo_Collection.postman_collection.json)** for ready-to-use API requests with:
- Automatic token extraction
- Environment variable management
- All CRUD operations pre-configured

### Swagger Documentation

- Company Service: http://localhost:8081/swagger-ui.html
- Job Service: http://localhost:8082/swagger-ui.html
- Application Service: http://localhost:8083/swagger-ui.html
