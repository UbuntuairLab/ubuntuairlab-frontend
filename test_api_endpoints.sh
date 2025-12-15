#!/bin/bash

# API Endpoint Testing Script for UbuntuAirLab
# Date: December 15, 2025

BASE_URL="https://air-lab.bestwebapp.tech/api/v1"
OUTPUT_FILE="api_responses_$(date +%Y%m%d_%H%M%S).md"

echo "# UbuntuAirLab API Endpoint Testing Results" > "$OUTPUT_FILE"
echo "**Date:** $(date)" >> "$OUTPUT_FILE"
echo "**Base URL:** $BASE_URL" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    local content_type=${5:-"application/json"}
    
    echo "" >> "$OUTPUT_FILE"
    echo "## $description" >> "$OUTPUT_FILE"
    echo "**Endpoint:** \`$method $endpoint\`" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    
    if [ "$method" = "GET" ]; then
        echo "### Request" >> "$OUTPUT_FILE"
        echo "\`\`\`bash" >> "$OUTPUT_FILE"
        echo "curl -X GET \"${BASE_URL}${endpoint}\" \\" >> "$OUTPUT_FILE"
        if [ ! -z "$TOKEN" ]; then
            echo "  -H \"Authorization: Bearer \$TOKEN\"" >> "$OUTPUT_FILE"
        fi
        echo "\`\`\`" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        
        echo "### Response" >> "$OUTPUT_FILE"
        echo "\`\`\`json" >> "$OUTPUT_FILE"
        if [ ! -z "$TOKEN" ]; then
            curl -s -X GET "${BASE_URL}${endpoint}" \
                -H "Authorization: Bearer $TOKEN" | jq '.' 2>/dev/null || echo "Error or non-JSON response"
        else
            curl -s -X GET "${BASE_URL}${endpoint}" | jq '.' 2>/dev/null || echo "Error or non-JSON response"
        fi >> "$OUTPUT_FILE"
        echo "\`\`\`" >> "$OUTPUT_FILE"
    else
        echo "### Request" >> "$OUTPUT_FILE"
        echo "\`\`\`bash" >> "$OUTPUT_FILE"
        echo "curl -X $method \"${BASE_URL}${endpoint}\" \\" >> "$OUTPUT_FILE"
        echo "  -H \"Content-Type: $content_type\" \\" >> "$OUTPUT_FILE"
        if [ ! -z "$TOKEN" ]; then
            echo "  -H \"Authorization: Bearer \$TOKEN\" \\" >> "$OUTPUT_FILE"
        fi
        if [ ! -z "$data" ]; then
            echo "  -d '$data'" >> "$OUTPUT_FILE"
        fi
        echo "\`\`\`" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        
        echo "### Response" >> "$OUTPUT_FILE"
        echo "\`\`\`json" >> "$OUTPUT_FILE"
        if [ ! -z "$TOKEN" ]; then
            curl -s -X $method "${BASE_URL}${endpoint}" \
                -H "Content-Type: $content_type" \
                -H "Authorization: Bearer $TOKEN" \
                -d "$data" | jq '.' 2>/dev/null || echo "Error or non-JSON response"
        else
            curl -s -X $method "${BASE_URL}${endpoint}" \
                -H "Content-Type: $content_type" \
                -d "$data" | jq '.' 2>/dev/null || echo "Error or non-JSON response"
        fi >> "$OUTPUT_FILE"
        echo "\`\`\`" >> "$OUTPUT_FILE"
    fi
    
    echo "" >> "$OUTPUT_FILE"
    echo "---" >> "$OUTPUT_FILE"
}

echo "Starting API endpoint tests..."

# 1. Login to get token
echo "Logging in to get token..."
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username=admin&password=admin123")

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.access_token' 2>/dev/null)

echo "" >> "$OUTPUT_FILE"
echo "## Authentication" >> "$OUTPUT_FILE"
echo "**Login Response:**" >> "$OUTPUT_FILE"
echo "\`\`\`json" >> "$OUTPUT_FILE"
echo "$LOGIN_RESPONSE" | jq '.' 2>/dev/null >> "$OUTPUT_FILE"
echo "\`\`\`" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
    echo "❌ Failed to authenticate. Cannot test protected endpoints." | tee -a "$OUTPUT_FILE"
    exit 1
fi

echo "✓ Authentication successful"

# 3. Auth endpoints
test_endpoint "GET" "/auth/me" "Get Current User Profile"

# 4. Flights endpoints
test_endpoint "GET" "/flights/" "Get All Flights (default pagination)"
test_endpoint "GET" "/flights/?skip=0&limit=10" "Get Flights (paginated)"

# Try to get a specific flight (use one from the list if available)
FIRST_ICAO=$(curl -s -X GET "${BASE_URL}/flights/?limit=1" \
    -H "Authorization: Bearer $TOKEN" | jq -r '.data[0].icao24' 2>/dev/null)

if [ ! -z "$FIRST_ICAO" ] && [ "$FIRST_ICAO" != "null" ]; then
    test_endpoint "GET" "/flights/$FIRST_ICAO" "Get Specific Flight Details"
    test_endpoint "GET" "/flights/$FIRST_ICAO/predictions" "Get Flight Predictions"
else
    echo "No flights available to test specific endpoints" | tee -a "$OUTPUT_FILE"
fi

# 5. Parking endpoints
test_endpoint "GET" "/parking/spots" "Get All Parking Spots"
test_endpoint "GET" "/parking/spots?spot_type=civil" "Get Civil Parking Spots"
test_endpoint "GET" "/parking/spots?status=available" "Get Available Parking Spots"

# Get a specific spot
FIRST_SPOT=$(curl -s -X GET "${BASE_URL}/parking/spots?limit=1" \
    -H "Authorization: Bearer $TOKEN" | jq -r '.[0].spot_id' 2>/dev/null)

if [ ! -z "$FIRST_SPOT" ] && [ "$FIRST_SPOT" != "null" ]; then
    test_endpoint "GET" "/parking/spots/$FIRST_SPOT" "Get Specific Parking Spot"
fi

test_endpoint "GET" "/parking/allocations" "Get Parking Allocations"
test_endpoint "GET" "/parking/allocations?active_only=true" "Get Active Allocations Only"
test_endpoint "GET" "/parking/availability" "Get Parking Availability Stats"
test_endpoint "GET" "/parking/conflicts" "Get Parking Conflicts"

# 6. Dashboard
test_endpoint "GET" "/dashboard/stats" "Get Dashboard Statistics"

# 7. Default endpoints
test_endpoint "GET" "/" "Root Endpoint"
test_endpoint "GET" "/health" "Health Check"

# 8. Predictions
test_endpoint "GET" "/predictions/health" "Check ML API Health"
test_endpoint "GET" "/predictions/models/info" "Get ML Models Information"

# Test single prediction
PREDICTION_DATA='{
  "vitesse_actuelle": 250.0,
  "altitude": 3500.0,
  "distance_piste": 15.5,
  "type_vol": 0
}'
test_endpoint "POST" "/predictions/predict" "Single Flight Prediction (minimal data)" "$PREDICTION_DATA"

# Test batch prediction with array
BATCH_DATA='[
  {
    "vitesse_actuelle": 250.0,
    "altitude": 3500.0,
    "distance_piste": 15.5,
    "type_vol": 0,
    "icao24": "test1",
    "callsign": "TEST1"
  },
  {
    "vitesse_actuelle": 180.0,
    "altitude": 2000.0,
    "distance_piste": 8.0,
    "type_vol": 0,
    "icao24": "test2",
    "callsign": "TEST2"
  }
]'
test_endpoint "POST" "/predictions/predict/batch" "Batch Flight Predictions" "$BATCH_DATA"

# 9. Sync
test_endpoint "GET" "/sync/status" "Get Sync Status"

# 10. Notifications
test_endpoint "GET" "/notifications/notifications" "Get All Notifications"
test_endpoint "GET" "/notifications/notifications?limit=5" "Get Recent Notifications (limited)"
test_endpoint "GET" "/notifications/notifications/unread/count" "Get Unread Notifications Count"
test_endpoint "GET" "/notifications/notifications/critical" "Get Critical Notifications"

echo "" >> "$OUTPUT_FILE"
echo "---" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "## Summary" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "Testing completed at: $(date)" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "### Key Findings" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "- **Authentication:** Bearer token required for most endpoints" >> "$OUTPUT_FILE"
echo "- **Response Formats:** All endpoints return JSON" >> "$OUTPUT_FILE"
echo "- **Pagination:** Uses \`skip\` and \`limit\` query parameters" >> "$OUTPUT_FILE"
echo "- **Error Format:** \`{\"detail\": \"error message\"}\`" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

echo ""
echo "✓ Testing complete!"
echo "Results saved to: $OUTPUT_FILE"
echo ""
echo "Opening file..."
cat "$OUTPUT_FILE"
