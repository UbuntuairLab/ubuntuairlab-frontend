package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for dashboard statistics endpoint
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardStats {
    
    @JsonProperty("active_flights")
    private int activeFlights;
    
    @JsonProperty("arrivals_today")
    private int arrivalsToday;
    
    @JsonProperty("departures_today")
    private int departuresToday;
    
    @JsonProperty("parking_utilization")
    private double parkingUtilization;
    
    @JsonProperty("average_turnaround")
    private double averageTurnaround;
    
    @JsonProperty("delays_count")
    private int delaysCount;
    
    @JsonProperty("conflicts_detected")
    private int conflictsDetected;
    
    public DashboardStats() {}
    
    public int getActiveFlights() {
        return activeFlights;
    }
    
    public void setActiveFlights(int activeFlights) {
        this.activeFlights = activeFlights;
    }
    
    public int getArrivalsToday() {
        return arrivalsToday;
    }
    
    public void setArrivalsToday(int arrivalsToday) {
        this.arrivalsToday = arrivalsToday;
    }
    
    public int getDeparturesToday() {
        return departuresToday;
    }
    
    public void setDeparturesToday(int departuresToday) {
        this.departuresToday = departuresToday;
    }
    
    public double getParkingUtilization() {
        return parkingUtilization;
    }
    
    public void setParkingUtilization(double parkingUtilization) {
        this.parkingUtilization = parkingUtilization;
    }
    
    public double getAverageTurnaround() {
        return averageTurnaround;
    }
    
    public void setAverageTurnaround(double averageTurnaround) {
        this.averageTurnaround = averageTurnaround;
    }
    
    public int getDelaysCount() {
        return delaysCount;
    }
    
    public void setDelaysCount(int delaysCount) {
        this.delaysCount = delaysCount;
    }
    
    public int getConflictsDetected() {
        return conflictsDetected;
    }
    
    public void setConflictsDetected(int conflictsDetected) {
        this.conflictsDetected = conflictsDetected;
    }
}
