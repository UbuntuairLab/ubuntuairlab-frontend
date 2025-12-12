# API Endpoints - AIGE-APRON-SMART v3.0

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication

### POST /auth/login
Authenticate user and receive JWT token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "john.doe",
    "email": "john.doe@aige.tg",
    "firstName": "John",
    "lastName": "Doe",
    "role": "ADMIN"
  },
  "message": "Connexion réussie"
}
```

### POST /auth/register
Register a new user (admin only).

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "role": "OPS"
}
```

---

## Flights

All flight endpoints require authentication via Bearer token in header:
```
Authorization: Bearer {token}
```

### GET /flights
Get list of active flights.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "icao24": "3c6444",
    "callsign": "AF1234",
    "aircraftType": "A320",
    "company": "Air France",
    "nature": "PAX",
    "status": "EN_ROUTE",
    "origin": "CDG",
    "destination": "LFW",
    "eta": "2024-12-10T16:30:00",
    "latitude": 6.5,
    "longitude": 1.8,
    "altitude": 10000,
    "heading": 180,
    "speed": 450,
    "passengers": 150,
    "assignedPosteId": null
  }
]
```

### GET /flights/{icao24}
Get flight details by ICAO24 transponder address.

**Path Parameter:**
- `icao24` - Aircraft ICAO24 address (e.g., "3c6444")

**Response (200 OK):** Single Flight object

### POST /flights/sync
Manually trigger flight data synchronization with OpenSky Network.

**Response (200 OK):**
```json
{
  "message": "Synchronization started",
  "timestamp": "2024-12-10T14:30:00"
}
```

---

## Parking

### GET /parking/allocations
Get current parking spot allocations.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "spotCode": "ST-01",
    "icao24": "3c6444",
    "callsign": "AF1234",
    "allocatedAt": "2024-12-10T14:00:00",
    "estimatedDeparture": "2024-12-10T18:00:00",
    "status": "OCCUPIED"
  }
]
```

---

## Admin - Parking Management

### GET /admin/parking/spots
Get list of all parking spots (admin only).

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "code": "ST-01",
    "type": "STANDARD",
    "zone": "A",
    "maxAircraftSize": 4,
    "hasJetBridge": true,
    "hasGroundPower": true,
    "isActive": true,
    "latitude": 6.1656,
    "longitude": 1.2544
  }
]
```

### POST /admin/parking/spots
Create a new parking spot (admin only).

**Request Body:**
```json
{
  "code": "ST-19",
  "type": "STANDARD",
  "zone": "C",
  "maxAircraftSize": 3,
  "hasJetBridge": false,
  "hasGroundPower": true,
  "isActive": true,
  "latitude": 6.1660,
  "longitude": 1.2550
}
```

**Response (201 Created):** Created ParkingSpot object

---

## Admin - Configuration

### GET /admin/config
Get current application configuration (admin only).

**Response (200 OK):**
```json
{
  "opensky": {
    "enabled": true,
    "updateInterval": 30,
    "boundingBox": {
      "minLat": 5.0,
      "maxLat": 7.0,
      "minLon": 0.0,
      "maxLon": 2.5
    }
  },
  "parking": {
    "autoAllocation": true,
    "standardSpots": 18,
    "militarySpots": 4
  }
}
```

### PATCH /admin/config
Update application configuration (admin only).

**Request Body:** Partial or full configuration object

**Response (200 OK):** Updated configuration object

---

## Monitoring

### GET /health
Health check endpoint (public, no authentication required).

**Response (200 OK):**
```json
{
  "status": "UP",
  "timestamp": "2024-12-10T14:30:00",
  "services": {
    "database": "UP",
    "opensky": "UP"
  }
}
```

### GET /metrics
Prometheus metrics endpoint (public, no authentication required).

**Response (200 OK):** Prometheus format metrics

```
# HELP http_requests_total Total HTTP requests
# TYPE http_requests_total counter
http_requests_total{method="GET",endpoint="/flights"} 1234
```

---

## Error Responses

All endpoints may return these error responses:

### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Invalid input data",
  "timestamp": "2024-12-10T14:30:00"
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing authentication token",
  "timestamp": "2024-12-10T14:30:00"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Insufficient permissions",
  "timestamp": "2024-12-10T14:30:00"
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Resource not found",
  "timestamp": "2024-12-10T14:30:00"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "timestamp": "2024-12-10T14:30:00"
}
```

---

## Java Services Implementation

The frontend implements these API calls through service classes:

- **AuthService**: `/auth/login`, `/auth/register`
- **FlightService**: `/flights`, `/flights/{icao24}`, `/flights/sync`
- **ParkingService**: `/parking/allocations`, `/admin/parking/spots`
- **AdminService**: `/admin/config`, `/health`, `/metrics`

All services extend `BaseApiService` which handles:
- HTTP client configuration
- JSON serialization/deserialization
- Authentication token management
- Error handling
