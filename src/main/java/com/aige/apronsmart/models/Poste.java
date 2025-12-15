package com.aige.apronsmart.models;

import java.time.LocalDateTime;

/**
 * Model representing a parking poste (stand)
 */
public class Poste {
    private Long id;
    private String code; // ST-01 to ST-18, PM-01 to PM-04
    private PosteType type;
    private PosteStatus status;
    private PosteZone zone; // A, B, C, MILITARY
    
    private Integer maxCode; // Aircraft size code (1-6)
    private Boolean hasJetBridge;
    private Boolean hasGroundPower;
    private Boolean hasPreconditionedAir;
    private Boolean hasFuelService;
    
    private Double latitude;
    private Double longitude;
    
    private Long occupiedByFlightId;
    private String occupiedByCallsign;
    private LocalDateTime occupiedSince;
    private LocalDateTime reservedUntil;
    
    private String constraints; // Special constraints or notes
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // No-arg constructor
    public Poste() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public PosteType getType() {
        return type;
    }
    
    public void setType(PosteType type) {
        this.type = type;
    }
    
    public PosteStatus getStatus() {
        return status;
    }
    
    public void setStatus(PosteStatus status) {
        this.status = status;
    }
    
    public PosteZone getZone() {
        return zone;
    }
    
    public void setZone(PosteZone zone) {
        this.zone = zone;
    }
    
    public Integer getMaxCode() {
        return maxCode;
    }
    
    public void setMaxCode(Integer maxCode) {
        this.maxCode = maxCode;
    }
    
    public Boolean getHasJetBridge() {
        return hasJetBridge;
    }
    
    public void setHasJetBridge(Boolean hasJetBridge) {
        this.hasJetBridge = hasJetBridge;
    }
    
    public Boolean getHasGroundPower() {
        return hasGroundPower;
    }
    
    public void setHasGroundPower(Boolean hasGroundPower) {
        this.hasGroundPower = hasGroundPower;
    }
    
    public Boolean getHasPreconditionedAir() {
        return hasPreconditionedAir;
    }
    
    public void setHasPreconditionedAir(Boolean hasPreconditionedAir) {
        this.hasPreconditionedAir = hasPreconditionedAir;
    }
    
    public Boolean getHasFuelService() {
        return hasFuelService;
    }
    
    public void setHasFuelService(Boolean hasFuelService) {
        this.hasFuelService = hasFuelService;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Long getOccupiedByFlightId() {
        return occupiedByFlightId;
    }
    
    public void setOccupiedByFlightId(Long occupiedByFlightId) {
        this.occupiedByFlightId = occupiedByFlightId;
    }
    
    public String getOccupiedByCallsign() {
        return occupiedByCallsign;
    }
    
    public void setOccupiedByCallsign(String occupiedByCallsign) {
        this.occupiedByCallsign = occupiedByCallsign;
    }
    
    public LocalDateTime getOccupiedSince() {
        return occupiedSince;
    }
    
    public void setOccupiedSince(LocalDateTime occupiedSince) {
        this.occupiedSince = occupiedSince;
    }
    
    public LocalDateTime getReservedUntil() {
        return reservedUntil;
    }
    
    public void setReservedUntil(LocalDateTime reservedUntil) {
        this.reservedUntil = reservedUntil;
    }
    
    public String getConstraints() {
        return constraints;
    }
    
    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public enum PosteType {
        PAX("Passagers"),
        CARGO("Fret"),
        VIP("VIP"),
        MILITARY("Militaire"),
        GENERAL("Général");
        
        private final String displayName;
        
        PosteType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PosteStatus {
        LIBRE("Libre", "#4CAF50"),
        OCCUPE("Occupé", "#F44336"),
        RESERVE("Réservé", "#FF9800"),
        MAINTENANCE("Maintenance", "#9E9E9E"),
        MILITARY("Militaire", "#2196F3"),
        INDISPONIBLE("Indisponible", "#666666");
        
        private final String displayName;
        private final String colorHex;
        
        PosteStatus(String displayName, String colorHex) {
            this.displayName = displayName;
            this.colorHex = colorHex;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getColorHex() {
            return colorHex;
        }
    }
    
    public enum PosteZone {
        A("Zone A - Proche Terminal"),
        B("Zone B - Moyenne Distance"),
        C("Zone C - Éloignée"),
        MILITARY("Parking Militaire");
        
        private final String displayName;
        
        PosteZone(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
