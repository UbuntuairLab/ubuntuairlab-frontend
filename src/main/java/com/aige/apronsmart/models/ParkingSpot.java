package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model representing a parking spot (compatible with UbuntuAirLab API)
 * API Schema: {
 *   "spot_id": "C12",
 *   "spot_number": 12,
 *   "spot_type": "civil",
 *   "status": "available|occupied|maintenance",
 *   "aircraft_size_capacity": "small|medium|large",
 *   "has_jetway": true,
 *   "distance_to_terminal": 200
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingSpot {
    @JsonProperty("spot_id")
    private String spotId; // e.g., "C12", "M01"
    
    @JsonProperty("spot_number")
    private Integer spotNumber; // Numeric ID: 12, 1, etc.
    
    @JsonProperty("spot_type")
    private String spotType; // "civil" or "military"
    
    private String status; // "available", "occupied", "maintenance"
    
    @JsonProperty("aircraft_size_capacity")
    private String aircraftSizeCapacity; // "small", "medium", "large"
    
    @JsonProperty("has_jetway")
    private Boolean hasJetway;
    
    @JsonProperty("distance_to_terminal")
    private Integer distanceToTerminal;
    
    @JsonProperty("admin_configurable")
    private Boolean adminConfigurable;
    
    private String notes;
    
    // Legacy fields for backward compatibility
    private String code; // Maps to spotId
    private String type; // Maps to spotType
    
    @JsonProperty("capacity_category")
    private String capacityCategory; // Maps to aircraftSizeCapacity
    
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
    
    // Getters and Setters - API fields
    public String getSpotId() { return spotId; }
    public void setSpotId(String spotId) { 
        this.spotId = spotId;
        this.code = spotId; // Sync with legacy
    }
    
    public Integer getSpotNumber() { return spotNumber; }
    public void setSpotNumber(Integer spotNumber) { this.spotNumber = spotNumber; }
    
    public String getSpotType() { return spotType; }
    public void setSpotType(String spotType) { 
        this.spotType = spotType;
        this.type = spotType; // Sync with legacy
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAircraftSizeCapacity() { return aircraftSizeCapacity; }
    public void setAircraftSizeCapacity(String aircraftSizeCapacity) { 
        this.aircraftSizeCapacity = aircraftSizeCapacity;
        this.capacityCategory = aircraftSizeCapacity; // Sync with legacy
    }
    
    public Boolean getHasJetway() { return hasJetway; }
    public void setHasJetway(Boolean hasJetway) { this.hasJetway = hasJetway; }
    
    public Integer getDistanceToTerminal() { return distanceToTerminal; }
    public void setDistanceToTerminal(Integer distanceToTerminal) { this.distanceToTerminal = distanceToTerminal; }
    
    public Boolean getAdminConfigurable() { return adminConfigurable; }
    public void setAdminConfigurable(Boolean adminConfigurable) { this.adminConfigurable = adminConfigurable; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Legacy getters for backward compatibility
    public String getCode() { return code != null ? code : spotId; }
    public void setCode(String code) { this.code = code; }
    
    public String getType() { return type != null ? type : spotType; }
    public void setType(String type) { this.type = type; }
    
    public String getCapacityCategory() { return capacityCategory != null ? capacityCategory : aircraftSizeCapacity; }
    public void setCapacityCategory(String capacityCategory) { this.capacityCategory = capacityCategory; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    // Legacy getters for backward compatibility (fields already declared above)
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    
    public Integer getMaxAircraftSize() { return maxAircraftSize; }
    public void setMaxAircraftSize(Integer maxAircraftSize) { this.maxAircraftSize = maxAircraftSize; }
    
    public Boolean getHasJetBridge() { return hasJetBridge != null ? hasJetBridge : hasJetway; }
    public void setHasJetBridge(Boolean hasJetBridge) { this.hasJetBridge = hasJetBridge; }
    
    public Boolean getHasGroundPower() { return hasGroundPower; }
    public void setHasGroundPower(Boolean hasGroundPower) { this.hasGroundPower = hasGroundPower; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
