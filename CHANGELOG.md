# PostulyTn Project Changelog

## [Latest] - 2025-12-09

### 🔧 Fixed

#### Critical: JWT Authentication Not Working
- **Issue:** Protected endpoints (e.g., `/companies/1`) were returning 200 OK without JWT token
- **Root Cause:** Gateway filter name mismatch in `application.yml`
  - Incorrect: `- JwtAuthentication`
  - Correct: `- JwtAuth` (matches class name `JwtAuthGatewayFilterFactory`)
- **Files Changed:**
  - `gateway-service/src/main/resources/application.yml`
- **Impact:** All protected endpoints now properly require JWT authentication

#### Java Version Compatibility
- **Issue:** `ClassNotFoundException` when running services
- **Root Cause:** Project compiled with Java 25, but requires Java 17
- **Solution:** Added Java 17 requirement to documentation and startup scripts
- **Impact:** Services now compile and run correctly with Java 17

### ✨ Added

#### Automated Startup Script
- **File:** `run-services.sh`
- **Features:**
  - Automatically uses Java 17
  - Kills any existing services before starting
  - Starts all services in correct order with proper delays
  - Redirects logs to `./logs/` directory
  - Displays helpful status messages and service URLs

#### JWT Authentication Test Script
- **File:** `test-jwt-auth.sh`
- **Features:**
  - Tests protected endpoints without token (expects 401)
  - Tests public endpoints without token (expects 200/400)
  - Tests invalid token rejection (expects 401)
  - Provides clear pass/fail output

#### Enhanced Documentation
- **File:** `DEMO_TESTING_GUIDE.md`
- **Updates:**
  - Added Quick Start section
  - Added Java 17 prerequisite information
  - Added startup script usage
  - Added JWT authentication testing
  - Expanded troubleshooting section
  - Added helper scripts reference
  - Added service dependency diagram
  - Updated pre-demo checklist

### 🔄 Changed

#### Gateway Service Configuration
- Updated all route filters from `JwtAuthentication` to `JwtAuth`
- Routes affected: company-service, job-service, application-service

#### Build Process
- Project must now be built with Java 17
- Command: `export JAVA_HOME=$(/usr/libexec/java_home -v 17) && ./mvnw clean install -DskipTests`

#### Gitignore
- Added `logs/` directory to exclude service logs from version control

### 📋 Configuration Files

#### Files Created
```
run-services.sh          - Automated service startup
test-jwt-auth.sh         - JWT authentication testing
CHANGELOG.md             - This file
logs/                    - Service log directory (gitignored)
```

#### Files Modified
```
gateway-service/src/main/resources/application.yml  - Fixed filter names
DEMO_TESTING_GUIDE.md                                - Comprehensive updates
.gitignore                                           - Added logs/ directory
```

### 🔒 Security

#### JWT Filter Configuration
- **Public Endpoints (No Token Required):**
  - `/auth/login`
  - `/auth/register`
  - `/auth/refresh`
  - `/actuator/*`

- **Protected Endpoints (Token Required):**
  - All other endpoints require `Authorization: Bearer <token>`

### 📊 Service Ports

| Service | Port | Description |
|---------|------|-------------|
| Discovery (Eureka) | 8761 | Service registry |
| Config Server | 8888 | Configuration management |
| API Gateway | 8080 | Main entry point (JWT auth) |
| Company Service | 8081 | Company & recruiter management |
| Job Service | 8082 | Job posting management |
| Application Service | 8083 | Job application management |

### 🚀 Startup Order

**Required startup sequence:**
1. Discovery Service (8761) - Wait 10s
2. Config Service (8888) - Wait 5s
3. Gateway Service (8080) - Wait 5s
4. Business Services (8081, 8082, 8083) - Can start in parallel

**Automated:** Use `./run-services.sh`

### ✅ Testing

#### Quick Verification
```bash
# 1. Verify all services running
lsof -i :8761 -i :8888 -i :8080 -i :8081 -i :8082 -i :8083 | grep LISTEN

# 2. Check Eureka registration
open http://localhost:8761

# 3. Test JWT authentication
./test-jwt-auth.sh
```

### 🐛 Known Issues

#### Company Creation Bootstrap
- Company creation endpoint is protected, but registration requires a company
- **Workaround:** Create initial company directly via company-service (port 8081) to bypass gateway
- **Alternative:** Pre-populate database with seed data

### 📝 Notes

- **Java Version:** Java 17 is **required** (not compatible with Java 25)
- **Token Expiration:** Access tokens expire after 24 hours
- **Log Location:** Service logs saved to `./logs/` when using startup script
- **Service Registration:** Gateway may take 30-60s to appear in Eureka

---

## Migration Guide (for existing deployments)

If you have an existing deployment, follow these steps:

1. **Stop all services:**
   ```bash
   pkill -f 'java -jar'
   ```

2. **Ensure Java 17 is active:**
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   java -version  # Verify shows 17.x.x
   ```

3. **Rebuild with Java 17:**
   ```bash
   ./mvnw clean install -DskipTests
   ```

4. **Update gateway configuration** (if you modified it):
   - Change `- JwtAuthentication` to `- JwtAuth` in gateway-service/application.yml

5. **Restart services:**
   ```bash
   ./run-services.sh
   ```

6. **Verify JWT authentication:**
   ```bash
   ./test-jwt-auth.sh
   ```

---

**All changes have been tested and verified working as of 2025-12-09.**
