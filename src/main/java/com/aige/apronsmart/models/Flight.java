package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * Model representing a flight
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flight {
    private Long id;
    private String icao24; // Aircraft ICAO24 transponder address (primary identifier)
    private String callsign;
    private String aircraftType;
    private String company;
    private FlightNature nature; // PAX, CARGO, VIP, MIL
    private FlightStatus status;
    
    private String origin;
    private String destination;
    
    private LocalDateTime eta; // Estimated Time of Arrival
    private LocalDateTime etd; // Estimated Time of Departure
    private LocalDateTime ata; // Actual Time of Arrival
    private LocalDateTime atd; // Actual Time of Departure
    
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double heading;
    private Double speed;
    
    private Integer passengers;
    private Double cargoWeight;
    
    private Long assignedPosteId;
    private String assignedPosteCode;
    
    private String notes;
    private Boolean isEmergency;
    private Boolean isDelayed;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Flight() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIcao24() { return icao24; }
    public void setIcao24(String icao24) { this.icao24 = icao24; }
    
    public String getCallsign() { return callsign; }
    public void setCallsign(String callsign) { this.callsign = callsign; }
    
    public String getAircraftType() { return aircraftType; }
    public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }
    
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    
    public FlightNature getNature() { return nature; }
    public void setNature(FlightNature nature) { this.nature = nature; }
    
    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public LocalDateTime getEta() { return eta; }
    public void setEta(LocalDateTime eta) { this.eta = eta; }
    
    public LocalDateTime getEtd() { return etd; }
    public void setEtd(LocalDateTime etd) { this.etd = etd; }
    
    public LocalDateTime getAta() { return ata; }
    public void setAta(LocalDateTime ata) { this.ata = ata; }
    
    public LocalDateTime getAtd() { return atd; }
    public void setAtd(LocalDateTime atd) { this.atd = atd; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Double getAltitude() { return altitude; }
    public void setAltitude(Double altitude) { this.altitude = altitude; }
    
    public Double getHeading() { return heading; }
    public void setHeading(Double heading) { this.heading = heading; }
    
    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }
    
    public Integer getPassengers() { return passengers; }
    public void setPassengers(Integer passengers) { this.passengers = passengers; }
    
    public Double getCargoWeight() { return cargoWeight; }
    public void setCargoWeight(Double cargoWeight) { this.cargoWeight = cargoWeight; }
    
    public Long getAssignedPosteId() { return assignedPosteId; }
    public void setAssignedPosteId(Long assignedPosteId) { this.assignedPosteId = assignedPosteId; }
    
    public String getAssignedPosteCode() { return assignedPosteCode; }
    public void setAssignedPosteCode(String assignedPosteCode) { this.assignedPosteCode = assignedPosteCode; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Boolean getIsEmergency() { return isEmergency; }
    public void setIsEmergency(Boolean isEmergency) { this.isEmergency = isEmergency; }
    
    public Boolean getIsDelayed() { return isDelayed; }
    public void setIsDelayed(Boolean isDelayed) { this.isDelayed = isDelayed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public enum FlightNature {
        PAX("Passagers"),
        CARGO("Fret"),
        VIP("VIP"),
        MIL("Militaire");
        
        private final String displayName;
        
        FlightNature(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum FlightStatus {
        SCHEDULED("Planifié"),
        EN_ROUTE("En Route"),
        APPROACHING("En Approche"),
        LANDED("Atterri"),
        PARKED("Stationné"),
        DEPARTED("Parti"),
        CANCELLED("Annulé"),
        DELAYED("Retardé"),
        ACTIVE("Actif"); // Added to handle API's "active" status
        
        private final String displayName;
        
        FlightStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * Convert API status string to enum value
         * API returns: "active", "landed", "scheduled", "departed", "cancelled"
         * We map them to our enum values
         */
        @com.fasterxml.jackson.annotation.JsonCreator
        public static FlightStatus fromString(String value) {
            if (value == null) return null;
            
            // Handle lowercase API values
            String normalized = value.toLowerCase().trim();
            switch (normalized) {
                case "active":
                    return ACTIVE;
                case "scheduled":
                    return SCHEDULED;
                case "landed":
                    return LANDED;
                case "departed":
                    return DEPARTED;
                case "cancelled":
                case "canceled":
                    return CANCELLED;
                case "delayed":
                    return DELAYED;
                case "parked":
                    return PARKED;
                case "approaching":
                case "approach":
                    return APPROACHING;
                case "en_route":
                case "en route":
                case "enroute":
                    return EN_ROUTE;
                default:
                    // Log unknown status but don't fail
                    System.err.println("Unknown flight status from API: " + value + ", defaulting to ACTIVE");
                    return ACTIVE;
            }
        }
    }
}
