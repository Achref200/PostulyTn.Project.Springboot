#!/bin/bash

echo "JWT Authentication Test"
echo "======================="
echo ""

# Test 1: Accessing protected endpoint WITHOUT token (should fail with 401)
echo "Test 1: GET /companies/1 WITHOUT token"
echo "Expected: 401 Unauthorized"
echo "---"
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/companies/1)
if [ "$response" = "401" ]; then
    echo "✅ PASS: Got 401 Unauthorized (JWT auth is working!)"
else
    echo "❌ FAIL: Got $response (expected 401)"
fi
echo ""

# Test 2: Accessing public endpoint WITHOUT token (should succeed)
echo "Test 2: POST /auth/login WITHOUT token"
echo "Expected: 200 OK or 400 Bad Request (public endpoint)"
echo "---"
response=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"test"}' \
    http://localhost:8080/auth/login)
if [ "$response" = "200" ] || [ "$response" = "400" ] || [ "$response" = "404" ]; then
    echo "✅ PASS: Got $response (public endpoint is accessible)"
else
    echo "❌ FAIL: Got $response (public endpoint should be accessible)"
fi
echo ""

# Test 3: With invalid token (should fail with 401)
echo "Test 3: GET /companies/1 WITH invalid token"
echo "Expected: 401 Unauthorized"
echo "---"
response=$(curl -s -o /dev/null -w "%{http_code}" \
    -H "Authorization: Bearer invalid.token.here" \
    http://localhost:8080/companies/1)
if [ "$response" = "401" ]; then
    echo "✅ PASS: Got 401 Unauthorized (invalid token rejected)"
else
    echo "❌ FAIL: Got $response (expected 401)"
fi
echo ""

echo "======================="
echo "Test Complete!"
echo ""
echo "To get a valid token:"
echo "1. Register a recruiter: POST /auth/register"
echo "2. Login: POST /auth/login"
echo "3. Use the returned JWT token in Authorization header"
