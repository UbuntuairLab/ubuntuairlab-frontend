package com.aige.apronsmart.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service for OpenSky Network API integration
 * Free, open-source flight tracking data
 * https://openskynetwork.github.io/opensky-api/
 */
public class OpenSkyService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenSkyService.class);
    private static final String OPENSKY_API_URL = "https://opensky-network.org/api";
    
    private static OpenSkyService instance;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient;
    
    // Lomé Airport coordinates (Gnassingbé Eyadéma International Airport)
    private static final double LOME_LAT = 6.1656;
    private static final double LOME_LON = 1.2544;
    private static final double RADIUS_KM = 300; // 300km radius around airport
    
    private OpenSkyService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }
    
    public static OpenSkyService getInstance() {
        if (instance == null) {
            instance = new OpenSkyService();
        }
        return instance;
    }
    
    /**
     * Get all flights within radius of Lomé airport
     * Uses bounding box calculation
     */
    public List<Map<String, Object>> getFlightsNearAirport() throws IOException {
        // Calculate bounding box (approximate)
        double latDelta = RADIUS_KM / 111.0; // 1 degree lat ≈ 111km
        double lonDelta = RADIUS_KM / (111.0 * Math.cos(Math.toRadians(LOME_LAT)));
        
        double minLat = LOME_LAT - latDelta;
        double maxLat = LOME_LAT + latDelta;
        double minLon = LOME_LON - lonDelta;
        double maxLon = LOME_LON + lonDelta;
        
        String url = String.format("%s/states/all?lamin=%.4f&lomin=%.4f&lamax=%.4f&lomax=%.4f",
                OPENSKY_API_URL, minLat, minLon, maxLat, maxLon);
        
        logger.info("Fetching flights from OpenSky Network ({}km radius around Lomé)", RADIUS_KM);
        logger.debug("Bounding box: lat[{} to {}], lon[{} to {}]", minLat, maxLat, minLon, maxLon);
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("OpenSky API error: {}", response.code());
                throw new IOException("OpenSky API error: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            
            List<Map<String, Object>> flights = new ArrayList<>();
            JsonNode states = root.get("states");
            
            if (states != null && states.isArray()) {
                for (JsonNode state : states) {
                    Map<String, Object> flight = parseOpenSkyState(state);
                    if (flight != null) {
                        flights.add(flight);
                    }
                }
            }
            
            logger.info("Found {} flights near Lomé airport", flights.size());
            return flights;
        }
    }
    
    /**
     * Parse OpenSky state vector
     * Format: [icao24, callsign, origin_country, time_position, last_contact, 
     *          longitude, latitude, baro_altitude, on_ground, velocity, 
     *          true_track, vertical_rate, sensors, geo_altitude, squawk, spi, position_source]
     */
    private Map<String, Object> parseOpenSkyState(JsonNode state) {
        if (state == null || !state.isArray() || state.size() < 17) {
            return null;
        }
        
        Map<String, Object> flight = new HashMap<>();
        
        // ICAO24 (index 0)
        JsonNode icao24Node = state.get(0);
        if (icao24Node != null && !icao24Node.isNull()) {
            flight.put("icao24", icao24Node.asText().trim());
        }
        
        // Callsign (index 1)
        JsonNode callsignNode = state.get(1);
        if (callsignNode != null && !callsignNode.isNull()) {
            flight.put("callsign", callsignNode.asText().trim());
        }
        
        // Origin country (index 2)
        JsonNode countryNode = state.get(2);
        if (countryNode != null && !countryNode.isNull()) {
            flight.put("origin_country", countryNode.asText());
        }
        
        // Longitude (index 5) and Latitude (index 6)
        JsonNode lonNode = state.get(5);
        JsonNode latNode = state.get(6);
        if (lonNode != null && !lonNode.isNull() && latNode != null && !latNode.isNull()) {
            flight.put("longitude", lonNode.asDouble());
            flight.put("latitude", latNode.asDouble());
        } else {
            // Skip flights without position
            return null;
        }
        
        // Barometric altitude (index 7) - meters
        JsonNode altNode = state.get(7);
        if (altNode != null && !altNode.isNull()) {
            flight.put("altitude", altNode.asDouble());
        }
        
        // On ground (index 8)
        JsonNode onGroundNode = state.get(8);
        if (onGroundNode != null && !onGroundNode.isNull()) {
            boolean onGround = onGroundNode.asBoolean();
            flight.put("on_ground", onGround);
            flight.put("status", onGround ? "landed" : "active");
        }
        
        // Velocity (index 9) - m/s
        JsonNode velocityNode = state.get(9);
        if (velocityNode != null && !velocityNode.isNull()) {
            flight.put("speed", velocityNode.asDouble());
        }
        
        // True track / heading (index 10) - degrees
        JsonNode trackNode = state.get(10);
        if (trackNode != null && !trackNode.isNull()) {
            flight.put("heading", trackNode.asDouble());
        }
        
        // Vertical rate (index 11) - m/s
        JsonNode verticalRateNode = state.get(11);
        if (verticalRateNode != null && !verticalRateNode.isNull()) {
            flight.put("vertical_rate", verticalRateNode.asDouble());
        }
        
        // Calculate distance from airport
        double distance = calculateDistance(
                LOME_LAT, LOME_LON,
                flight.get("latitude") != null ? (Double) flight.get("latitude") : 0,
                flight.get("longitude") != null ? (Double) flight.get("longitude") : 0
        );
        flight.put("distance_from_airport_km", Math.round(distance * 10.0) / 10.0);
        
        return flight;
    }
    
    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * Get detailed flight information by ICAO24
     */
    public Map<String, Object> getFlightByIcao24(String icao24) throws IOException {
        String url = OPENSKY_API_URL + "/states/all?icao24=" + icao24;
        
        logger.info("Fetching flight details for ICAO24: {}", icao24);
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenSky API error: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode states = root.get("states");
            
            if (states != null && states.isArray() && states.size() > 0) {
                return parseOpenSkyState(states.get(0));
            }
            
            return null;
        }
    }
}
