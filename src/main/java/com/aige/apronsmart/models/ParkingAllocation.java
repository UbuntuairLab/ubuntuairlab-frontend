package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * Model representing a parking spot allocation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingAllocation {
    private Long id;
    private String spotCode; // e.g., "ST-01", "PM-01"
    private String icao24; // Aircraft ICAO24 address
    private String callsign;
    private LocalDateTime allocatedAt;
    private LocalDateTime estimatedDeparture;
    private String status; // ALLOCATED, OCCUPIED, RELEASED
    
    // Constructors
    public ParkingAllocation() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSpotCode() { return spotCode; }
    public void setSpotCode(String spotCode) { this.spotCode = spotCode; }
    
    public String getIcao24() { return icao24; }
    public void setIcao24(String icao24) { this.icao24 = icao24; }
    
    public String getCallsign() { return callsign; }
    public void setCallsign(String callsign) { this.callsign = callsign; }
    
    public LocalDateTime getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(LocalDateTime allocatedAt) { this.allocatedAt = allocatedAt; }
    
    public LocalDateTime getEstimatedDeparture() { return estimatedDeparture; }
    public void setEstimatedDeparture(LocalDateTime estimatedDeparture) { this.estimatedDeparture = estimatedDeparture; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
