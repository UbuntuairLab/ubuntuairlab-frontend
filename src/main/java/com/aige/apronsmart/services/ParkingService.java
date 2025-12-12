package com.aige.apronsmart.services;

import com.aige.apronsmart.models.ParkingAllocation;
import com.aige.apronsmart.models.ParkingAvailability;
import com.aige.apronsmart.models.ParkingSpot;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for parking management operations with UbuntuAirLab API
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
        String responseStr = httpClient.newCall(
                buildRequest("/parking/spots").get().build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, new TypeReference<List<ParkingSpot>>() {});
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
     * Assign a specific parking spot to a flight
     * @param icao24 Flight ICAO24 identifier
     * @param spotNumber Parking spot number
     * @return Assignment result
     */
    public Map<String, Object> assignParking(String icao24, String spotNumber) throws IOException {
        logger.info("Assigning parking spot {} to flight: {}", spotNumber, icao24);
        Map<String, String> requestData = new HashMap<>();
        requestData.put("icao24", icao24);
        requestData.put("spot_number", spotNumber);
        
        String responseStr = httpClient.newCall(
                buildRequest("/parking/assign")
                    .post(okhttp3.RequestBody.create(
                        objectMapper.writeValueAsString(requestData), 
                        JSON
                    ))
                    .build()
        ).execute().body().string();
        
        return objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
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
