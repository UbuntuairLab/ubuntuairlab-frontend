package com.aige.apronsmart.services;

import com.aige.apronsmart.models.ParkingAllocation;
import com.aige.apronsmart.models.ParkingAvailability;
import com.aige.apronsmart.models.ParkingSpot;
import com.aige.apronsmart.models.ParkingSpotsResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for parking management operations with UbuntuAirLab API
 * 
 * API Integration Notes:
 * - Base URL: https://air-lab.bestwebapp.tech/api/v1
 * - Authentication: Bearer token required
 * - Parking assignment uses AUTOMATIC allocation algorithm (no manual spot selection)
 * - API intelligently selects best available spot based on flight data
 * 
 * Key Endpoints:
 * - GET /parking/spots - Returns direct array of parking spots
 * - POST /parking/assign - Auto-allocates parking (requires icao24 only)
 * - GET /parking/availability - Real-time availability stats
 * 
 * @see <a href="https://air-lab.bestwebapp.tech/docs">API Documentation</a>
 */
public class ParkingService extends BaseApiService {
    
    private static ParkingService instance;
    
    private ParkingService() {}
    
    public static ParkingService getInstance() {
        if (instance == null) {
            instance = new ParkingService();
        }
        return instance;
    }
    
    /**
     * Get all parking spots
     * @return List of parking spots
     */
    public List<ParkingSpot> getAllParkingSpots() throws IOException {
        logger.info("Fetching all parking spots");
        okhttp3.Response response = httpClient.newCall(
                buildRequest("/parking/spots").get().build()
        ).execute();
        
        String responseStr = response.body().string();
        logger.debug("Parking spots API response ({}): {}", response.code(), 
                     responseStr.substring(0, Math.min(200, responseStr.length())));
        
        if (!response.isSuccessful()) {
            logger.error("Failed to fetch parking spots: HTTP {}", response.code());
            throw new IOException("Failed to fetch parking spots: HTTP " + response.code());
        }
        
        // API returns direct array: [{"spot_id": "C12", ...}, ...]
        try {
            JsonNode rootNode = objectMapper.readTree(responseStr);
            
            // Primary format: direct array
            if (rootNode.isArray()) {
                return objectMapper.readValue(responseStr, new TypeReference<List<ParkingSpot>>() {});
            }
            
            // Fallback: check for wrapped responses (future-proofing)
            if (rootNode.has("data") || rootNode.has("spots")) {
                JsonNode dataNode = rootNode.has("data") ? rootNode.get("data") : rootNode.get("spots");
                return objectMapper.convertValue(dataNode, new TypeReference<List<ParkingSpot>>() {});
            }
            
            logger.error("Unexpected response format: {}", responseStr.substring(0, 100));
            throw new IOException("Unexpected response format from parking spots API");
            
        } catch (Exception e) {
            logger.error("Failed to parse parking spots response: {}", e.getMessage());
            throw new IOException("Invalid response format from parking spots API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get current parking allocations
     * @return List of parking allocations
     */
    public List<ParkingAllocation> getAllocations() throws IOException {
        logger.info("Fetching parking allocations");
        String responseStr = httpClient.newCall(
                buildRequest("/parking/allocations").get().build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, new TypeReference<List<ParkingAllocation>>() {});
    }
    
    /**
     * Get parking availability statistics
     * @return ParkingAvailability with real-time stats
     */
    public ParkingAvailability getAvailability() throws IOException {
        logger.info("Fetching parking availability");
        return get("/parking/availability", ParkingAvailability.class);
    }
    
    /**
     * Get detected parking conflicts
     * @return List of conflicts
     */
    public List<Map<String, Object>> getConflicts() throws IOException {
        logger.info("Fetching parking conflicts");
        String responseStr = httpClient.newCall(
                buildRequest("/parking/conflicts").get().build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, new TypeReference<List<Map<String, Object>>>() {});
    }
    
    /**
     * Automatically allocate parking for a flight
     * @param icao24 Flight ICAO24 identifier
     * @return Allocation result with assigned spot
     */
    public Map<String, Object> allocateParking(String icao24) throws IOException {
        logger.info("Allocating parking for flight: {}", icao24);
        Map<String, String> requestData = new HashMap<>();
        requestData.put("icao24", icao24);
        
        String responseStr = httpClient.newCall(
                buildRequest("/parking/allocate")
                    .post(okhttp3.RequestBody.create(
                        objectMapper.writeValueAsString(requestData), 
                        JSON
                    ))
                    .build()
        ).execute().body().string();
        
        return objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
    }
    
    /**
     * Automatically allocate a parking spot to a flight
     * API uses intelligent allocation algorithm
     * @param icao24 Flight ICAO24 identifier
     * @return Assignment result with allocated spot
     */
    public Map<String, Object> assignParking(String icao24) throws IOException {
        return assignParking(icao24, false);
    }
    
    /**
     * Automatically allocate a parking spot to a flight
     * @param icao24 Flight ICAO24 identifier
     * @param manualOverride Force allocation even if conflicts exist
     * @return Assignment result with allocated spot
     */
    public Map<String, Object> assignParking(String icao24, boolean manualOverride) throws IOException {
        logger.info("Auto-allocating parking for flight: {} (override: {})", icao24, manualOverride);
        
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("icao24", icao24);
        if (manualOverride) {
            requestData.put("manual_override", true);
        }
        
        okhttp3.Response response = httpClient.newCall(
                buildRequest("/parking/assign")
                    .post(okhttp3.RequestBody.create(
                        objectMapper.writeValueAsString(requestData), 
                        JSON
                    ))
                    .build()
        ).execute();
        
        String responseStr = response.body().string();
        logger.debug("Assignment response ({}): {}", response.code(), responseStr);
        
        // Handle error responses
        if (!response.isSuccessful()) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("status_code", response.code());
            
            // Try to parse as JSON (API returns {"detail": "error message"})
            try {
                Map<String, Object> errorData = objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
                errorResult.put("detail", errorData.getOrDefault("detail", "Unknown error"));
                return errorResult;
            } catch (Exception e) {
                // Plain text error response
                errorResult.put("detail", responseStr.isEmpty() ? "Unknown error" : responseStr);
                return errorResult;
            }
        }
        
        // Parse successful response: {"success": true, "spot_id": "C12", ...}
        try {
            Map<String, Object> result = objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
            result.put("success", true);
            return result;
        } catch (Exception e) {
            logger.error("Failed to parse assignment response: {}", e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("detail", "Invalid response format: " + responseStr);
            return errorResult;
        }
    }
    
    /**
     * Get a specific parking allocation
     * @param allocationId Allocation ID
     * @return Allocation details
     */
    public ParkingAllocation getAllocation(Long allocationId) throws IOException {
        logger.info("Fetching allocation: {}", allocationId);
        return get("/parking/allocations/" + allocationId, ParkingAllocation.class);
    }
    
    /**
     * Get a specific parking spot
     * @param spotId Spot ID
     * @return Spot details
     */
    public ParkingSpot getSpot(Long spotId) throws IOException {
        logger.info("Fetching parking spot: {}", spotId);
        return get("/parking/spots/" + spotId, ParkingSpot.class);
    }
    
    /**
     * Civil aircraft recall operation
     * @param spotNumber Parking spot to clear
     * @return Operation result
     */
    public Map<String, Object> civilRecall(String spotNumber) throws IOException {
        logger.info("Civil recall for spot: {}", spotNumber);
        Map<String, String> requestData = new HashMap<>();
        requestData.put("spot_number", spotNumber);
        
        String responseStr = httpClient.newCall(
                buildRequest("/parking/civil-recall")
                    .post(okhttp3.RequestBody.create(
                        objectMapper.writeValueAsString(requestData), 
                        JSON
                    ))
                    .build()
        ).execute().body().string();
        
        return objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
    }
    
    /**
     * Military aircraft transfer operation
     * @param icao24 Military aircraft ICAO24
     * @return Transfer result
     */
    public Map<String, Object> militaryTransfer(String icao24) throws IOException {
        logger.info("Military transfer for aircraft: {}", icao24);
        Map<String, String> requestData = new HashMap<>();
        requestData.put("icao24", icao24);
        
        String responseStr = httpClient.newCall(
                buildRequest("/parking/military-transfer")
                    .post(okhttp3.RequestBody.create(
                        objectMapper.writeValueAsString(requestData), 
                        JSON
                    ))
                    .build()
        ).execute().body().string();
        
        return objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
    }
    
    /**
     * Get parking spot by spot_id (string identifier like "C12")
     * @param spotId Spot identifier (e.g., "C12", "M01")
     * @return Parking spot details
     */
    public ParkingSpot getSpotById(String spotId) throws IOException {
        logger.info("Fetching parking spot by ID: {}", spotId);
        return get("/parking/spots/" + spotId, ParkingSpot.class);
    }
    
    /**
     * Update a parking spot (Admin only)
     * @param spotId Spot identifier
     * @param updates Map with fields to update (status, notes)
     * @return Updated parking spot
     */
    public ParkingSpot updateSpot(String spotId, Map<String, Object> updates) throws IOException {
        logger.info("Updating parking spot: {}", spotId);
        String responseStr = httpClient.newCall(
                buildRequest("/parking/spots/" + spotId)
                    .patch(okhttp3.RequestBody.create(
                        objectMapper.writeValueAsString(updates), 
                        JSON
                    ))
                    .build()
        ).execute().body().string();
        
        return objectMapper.readValue(responseStr, ParkingSpot.class);
    }
    
    /**
     * Create a new parking spot (Admin only)
     * @param spotData Map with spot data (spot_id, spot_number, spot_type, etc.)
     * @return Created parking spot
     */
    public ParkingSpot createSpot(Map<String, Object> spotData) throws IOException {
        logger.info("Creating new parking spot: {}", spotData.get("spot_id"));
        String responseStr = httpClient.newCall(
                buildRequest("/parking/spots")
                    .post(okhttp3.RequestBody.create(
                        objectMapper.writeValueAsString(spotData), 
                        JSON
                    ))
                    .build()
        ).execute().body().string();
        
        return objectMapper.readValue(responseStr, ParkingSpot.class);
    }
    
    /**
     * Delete a parking spot (Admin only)
     * @param spotId Spot identifier to delete
     */
    public void deleteSpot(String spotId) throws IOException {
        logger.info("Deleting parking spot: {}", spotId);
        httpClient.newCall(
                buildRequest("/parking/spots/" + spotId)
                    .delete()
                    .build()
        ).execute();
    }
    
    /**
     * Get parking spots with filters
     * @param spotType Filter by type ("civil" or "military")
     * @param status Filter by status ("available", "occupied", "maintenance")
     * @param skip Pagination offset
     * @param limit Pagination limit
     * @return List of parking spots
     */
    public List<ParkingSpot> getParkingSpots(String spotType, String status, int skip, int limit) throws IOException {
        StringBuilder url = new StringBuilder("/parking/spots?");
        if (spotType != null && !spotType.isEmpty()) {
            url.append("spot_type=").append(spotType).append("&");
        }
        if (status != null && !status.isEmpty()) {
            url.append("status=").append(status).append("&");
        }
        url.append("skip=").append(skip).append("&limit=").append(limit);
        
        logger.info("Fetching parking spots with filters: type={}, status={}", spotType, status);
        String responseStr = httpClient.newCall(
                buildRequest(url.toString()).get().build()
        ).execute().body().string();
        
        return objectMapper.readValue(responseStr, new TypeReference<List<ParkingSpot>>() {});
    }
}
