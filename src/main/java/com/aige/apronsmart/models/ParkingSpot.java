package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model representing a parking spot (compatible with UbuntuAirLab API)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingSpot {
    private Integer id;
    
    @JsonProperty("spot_number")
    private String spotNumber; // e.g., "N1", "P2", "S10A"
    
    private String code; // Legacy field
    
    private String type; // commercial, cargo, military
    
    private String status; // available, occupied, maintenance
    
    @JsonProperty("capacity_category")
    private String capacityCategory; // small, medium, large
    
    @JsonProperty("is_active")
    private Boolean isActive;
    
    private String zone; // A, B, C, MILITARY - Legacy
    
    private Integer maxAircraftSize; // Code 1-6 - Legacy
    private Boolean hasJetBridge; // Legacy
    private Boolean hasGroundPower; // Legacy
    
    // Position
    private Double latitude;
    private Double longitude;
    
    // Constructors
    public ParkingSpot() {}
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getSpotNumber() { return spotNumber; }
    public void setSpotNumber(String spotNumber) { 
        this.spotNumber = spotNumber;
        this.code = spotNumber; // Sync with legacy field
    }
    
    public String getCode() { return code != null ? code : spotNumber; }
    public void setCode(String code) { 
        this.code = code;
        if (spotNumber == null) this.spotNumber = code;
    }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCapacityCategory() { return capacityCategory; }
    public void setCapacityCategory(String capacityCategory) { this.capacityCategory = capacityCategory; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    
    public Integer getMaxAircraftSize() { return maxAircraftSize; }
    public void setMaxAircraftSize(Integer maxAircraftSize) { this.maxAircraftSize = maxAircraftSize; }
    
    public Boolean getHasJetBridge() { return hasJetBridge; }
    public void setHasJetBridge(Boolean hasJetBridge) { this.hasJetBridge = hasJetBridge; }
    
    public Boolean getHasGroundPower() { return hasGroundPower; }
    public void setHasGroundPower(Boolean hasGroundPower) { this.hasGroundPower = hasGroundPower; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
