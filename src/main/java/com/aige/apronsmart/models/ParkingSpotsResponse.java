package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Wrapper for parking spots API response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingSpotsResponse {
    private List<ParkingSpot> data;
    private String status;
    private String message;
    
    // For array responses directly
    @JsonProperty("spots")
    private List<ParkingSpot> spots;
    
    public ParkingSpotsResponse() {}
    
    public List<ParkingSpot> getData() { 
        return data != null ? data : spots; 
    }
    
    public void setData(List<ParkingSpot> data) { 
        this.data = data; 
    }
    
    public List<ParkingSpot> getSpots() { 
        return spots; 
    }
    
    public void setSpots(List<ParkingSpot> spots) { 
        this.spots = spots; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String message) { 
        this.message = message; 
    }
}
