package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Response model for parking availability endpoint
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingAvailability {
    
    @JsonProperty("total_spots")
    private int totalSpots;
    
    private int available;
    private int occupied;
    private int maintenance;
    
    @JsonProperty("utilization_rate")
    private double utilizationRate;
    
    @JsonProperty("spots_by_type")
    private Map<String, SpotTypeStats> spotsByType;
    
    public static class SpotTypeStats {
        private int total;
        private int available;
        
        public SpotTypeStats() {}
        
        public int getTotal() {
            return total;
        }
        
        public void setTotal(int total) {
            this.total = total;
        }
        
        public int getAvailable() {
            return available;
        }
        
        public void setAvailable(int available) {
            this.available = available;
        }
    }
    
    public ParkingAvailability() {}
    
    public int getTotalSpots() {
        return totalSpots;
    }
    
    public void setTotalSpots(int totalSpots) {
        this.totalSpots = totalSpots;
    }
    
    public int getAvailable() {
        return available;
    }
    
    public void setAvailable(int available) {
        this.available = available;
    }
    
    public int getOccupied() {
        return occupied;
    }
    
    public void setOccupied(int occupied) {
        this.occupied = occupied;
    }
    
    public int getMaintenance() {
        return maintenance;
    }
    
    public void setMaintenance(int maintenance) {
        this.maintenance = maintenance;
    }
    
    public double getUtilizationRate() {
        return utilizationRate;
    }
    
    public void setUtilizationRate(double utilizationRate) {
        this.utilizationRate = utilizationRate;
    }
    
    public Map<String, SpotTypeStats> getSpotsByType() {
        return spotsByType;
    }
    
    public void setSpotsByType(Map<String, SpotTypeStats> spotsByType) {
        this.spotsByType = spotsByType;
    }
}
