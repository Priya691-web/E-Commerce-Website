#!/bin/bash

# Simple load testing script for FashionStore
# Simulates concurrent users, checkout, cart updates

BASE_URL="http://localhost:8080"
USERS=10
REQUESTS_PER_USER=20

echo "=== FashionStore Load Testing ==="
echo "Concurrent Users: $USERS"
echo "Requests per User: $REQUESTS_PER_USER"
echo "Total Requests: $((USERS * REQUESTS_PER_USER))"
echo ""

# Function to simulate a user session
simulate_user() {
    local user_id=$1
    local cookies_file="/tmp/cookies_$user_id.txt"
    
    echo "User $user_id: Starting session"
    
    # Login
    curl -s -c "$cookies_file" -X POST "$BASE_URL/login" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "email=admin@fashionstore.com&password=admin123" > /dev/null
    
    # Get CSRF token
    CSRF_TOKEN=$(curl -s -b "$cookies_file" "$BASE_URL/cart" | grep -o 'window.csrfToken = .*' | cut -d "'" -f 2)
    
    echo "User $user_id: CSRF token obtained"
    
    # Simulate cart operations
    for i in $(seq 1 $REQUESTS_PER_USER); do
        # Add to cart
        curl -s -b "$cookies_file" -X POST "$BASE_URL/cart" \
            -H "Content-Type: application/x-www-form-urlencoded" \
            -H "X-Requested-With: XMLHttpRequest" \
            -H "X-CSRF-Token: $CSRF_TOKEN" \
            -d "action=add&productId=1&quantity=1" > /dev/null
        
        # View cart
        curl -s -b "$cookies_file" "$BASE_URL/cart" > /dev/null
        
        # Random delay between requests (100-500ms)
        sleep 0.$((RANDOM % 4 + 1))
    done
    
    echo "User $user_id: Completed $REQUESTS_PER_USER requests"
    rm -f "$cookies_file"
}

# Start monitoring
echo "Starting load test..."
START_TIME=$(date +%s)

# Run concurrent users
for user_id in $(seq 1 $USERS); do
    simulate_user $user_id &
done

# Wait for all background jobs
wait

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo "=== Load Test Complete ==="
echo "Duration: ${DURATION}s"
echo "Throughput: $((USERS * REQUESTS_PER_USER / DURATION)) requests/second"
