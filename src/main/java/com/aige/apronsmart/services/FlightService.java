package com.aige.apronsmart.services;

import com.aige.apronsmart.models.Flight;
import com.aige.apronsmart.models.FlightsResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for flight operations with UbuntuAirLab API
 */
public class FlightService extends BaseApiService {
    
    private static FlightService instance;
    
    private FlightService() {}
    
    public static FlightService getInstance() {
        if (instance == null) {
            instance = new FlightService();
        }
        return instance;
    }
    
    /**
     * Get all flights with optional filters
     * @param status Flight status filter (active, landed, scheduled)
     * @param type Flight type filter (arrival, departure)
     * @param limit Maximum number of results
     * @param offset Offset for pagination
     * @param futureDate Date for future flights (YYYY-MM-DD format)
     * @return FlightsResponse with list of flights and pagination info
     */
    public FlightsResponse getFlights(String status, String type, Integer limit, Integer offset, String futureDate) throws IOException {
        StringBuilder endpoint = new StringBuilder("/flights?");
        
        if (status != null && !status.isEmpty()) {
            endpoint.append("status=").append(status).append("&");
        }
        if (type != null && !type.isEmpty()) {
            endpoint.append("flight_type=").append(type).append("&");
        }
        if (futureDate != null && !futureDate.isEmpty()) {
            endpoint.append("future_date=").append(futureDate).append("&");
        }
        if (limit != null) {
            endpoint.append("limit=").append(limit).append("&");
        }
        if (offset != null) {
            endpoint.append("skip=").append(offset).append("&");
        }
        
        logger.info("Fetching flights with filters - status: {}, type: {}, limit: {}, offset: {}", 
                    status, type, limit, offset);
        
        return get(endpoint.toString(), FlightsResponse.class);
    }
    
    /**
     * Get all flights without filters
     */
    public FlightsResponse getAllFlights() throws IOException {
        return getFlights(null, null, 100, 0, null);
    }
    
    /**
     * Get only active flights
     */
    public List<Flight> getActiveFlights() throws IOException {
        FlightsResponse response = getFlights("active", null, 100, 0, null);
        return response != null ? response.getFlights() : new ArrayList<>();
    }
    
    /**
     * Get arrivals only
     */
    public List<Flight> getArrivals() throws IOException {
        FlightsResponse response = getFlights(null, "arrival", 100, 0, null);
        return response != null ? response.getFlights() : new ArrayList<>();
    }
    
    /**
     * Get departures only
     */
    public List<Flight> getDepartures() throws IOException {
        FlightsResponse response = getFlights(null, "departure", 100, 0, null);
        return response != null ? response.getFlights() : new ArrayList<>();
    }
    
    /**
     * Get flight details by ICAO24 identifier
     * @param icao24 ICAO24 transponder address
     * @return Flight details
     */
    public Flight getFlightByIcao24(String icao24) throws IOException {
        logger.info("Fetching flight details for ICAO24: {}", icao24);
        return get("/flights/" + icao24, Flight.class);
    }
    
    /**
     * Get flight predictions by ICAO24
     * @param icao24 ICAO24 transponder address
     * @return Map containing predictions data
     */
    public Map<String, Object> getFlightPredictions(String icao24) throws IOException {
        logger.info("Fetching predictions for flight: {}", icao24);
        return get("/flights/" + icao24 + "/predictions", Map.class);
    }
    
    /**
     * Trigger manual synchronization of flight data from OpenSky Network
     * @return Response message
     */
    public Map<String, Object> triggerSync() throws IOException {
        logger.info("Triggering manual flight data synchronization");
        return post("/sync/trigger", null, Map.class);
    }
    
    /**
     * Get flights with advanced filters for history view
     * @param flightType arrival or departure
     * @param status Flight status
     * @param limit Max results
     * @param offset Pagination offset
     * @param futureDate Future date for scheduled flights (YYYY-MM-DD)
     * @return List of flights matching filters
     */
    public List<Flight> getFlightsWithFilters(String flightType, String status, Integer limit, Integer offset, String futureDate) throws IOException {
        StringBuilder endpoint = new StringBuilder("/flights?");
        
        if (flightType != null && !flightType.isEmpty()) {
            endpoint.append("flight_type=").append(flightType).append("&");
        }
        if (status != null && !status.isEmpty()) {
            endpoint.append("status=").append(status).append("&");
        }
        if (futureDate != null && !futureDate.isEmpty()) {
            endpoint.append("future_date=").append(futureDate).append("&");
        }
        if (limit != null) {
            endpoint.append("limit=").append(limit).append("&");
        }
        if (offset != null) {
            endpoint.append("skip=").append(offset).append("&");
        }
        
        logger.info("Fetching flights with filters - type: {}, status: {}, future_date: {}", 
                    flightType, status, futureDate);
        
        FlightsResponse response = get(endpoint.toString(), FlightsResponse.class);
        return response != null ? response.getFlights() : new ArrayList<>();
    }
}
