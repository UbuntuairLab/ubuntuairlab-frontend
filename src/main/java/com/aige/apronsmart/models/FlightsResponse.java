package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response model for flights list endpoint
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightsResponse {
    
    private List<Flight> flights;
    private int total;
    private int limit;
    private int offset;
    
    public FlightsResponse() {}
    
    public FlightsResponse(List<Flight> flights, int total, int limit, int offset) {
        this.flights = flights;
        this.total = total;
        this.limit = limit;
        this.offset = offset;
    }
    
    public List<Flight> getFlights() {
        return flights;
    }
    
    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
