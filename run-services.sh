#!/bin/bash

# Ensure Java 17 is used for running services
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

echo "Using Java: $(java -version 2>&1 | head -n 1)"
echo ""

# Function to run a service
run_service() {
    local service_name=$1
    local wait_time=$2
    echo "Starting $service_name..."
    cd "$service_name" && java -jar target/*.jar > "../logs/$service_name.log" 2>&1 &
    cd ..
    if [ -n "$wait_time" ]; then
        echo "Waiting ${wait_time}s for $service_name to start..."
        sleep "$wait_time"
    fi
}

# Create logs directory
mkdir -p logs

# Kill any existing services
echo "Stopping existing services..."
pkill -f 'java -jar' 2>/dev/null
sleep 2

# Start services in order
echo ""
echo "Starting PostulyTn Services with Java 17"
echo "========================================"
echo ""

run_service "discovery-service" 10
run_service "config-service" 5
run_service "gateway-service" 5
run_service "company-service" 3
run_service "job-service" 3
run_service "application-service" 3

echo ""
echo "✅ All services started!"
echo ""
echo "Service URLs:"
echo "  - Discovery (Eureka): http://localhost:8761"
echo "  - Config Server:      http://localhost:8888"
echo "  - API Gateway:        http://localhost:8080"
echo "  - Company Service:    http://localhost:8081"
echo "  - Job Service:        http://localhost:8082"
echo "  - Application Service: http://localhost:8083"
echo ""
echo "Logs are in: ./logs/"
echo "To stop all services: pkill -f 'java -jar'"
