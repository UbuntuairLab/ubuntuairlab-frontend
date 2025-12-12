package com.aige.apronsmart.services;

import com.aige.apronsmart.models.PredictionModels.PredictionRequest;
import com.aige.apronsmart.models.PredictionModels.PredictionResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Service for ML predictions with UbuntuAirLab API
 */
public class PredictionService extends BaseApiService {
    
    private static PredictionService instance;
    
    private PredictionService() {}
    
    public static PredictionService getInstance() {
        if (instance == null) {
            instance = new PredictionService();
        }
        return instance;
    }
    
    /**
     * Make ML prediction for a flight
     * @param request Prediction request with flight and context data
     * @return PredictionResponse with ML model predictions
     */
    public PredictionResponse predict(PredictionRequest request) throws IOException {
        logger.info("Making ML prediction for flight: {}", request.getCallsign());
        return post("/predictions/predict", request, PredictionResponse.class);
    }
    
    /**
     * Check ML API health status
     * @return Health status information
     */
    public Map<String, Object> checkHealth() throws IOException {
        logger.info("Checking ML API health");
        String responseStr = httpClient.newCall(
                buildRequest("/predictions/health").get().build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, Map.class);
    }
    
    /**
     * Get ML models information
     * @return Models information
     */
    public Map<String, Object> getModelsInfo() throws IOException {
        logger.info("Fetching ML models information");
        String responseStr = httpClient.newCall(
                buildRequest("/predictions/models/info").get().build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, Map.class);
    }
    
    /**
     * Make batch ML predictions for multiple flights
     * @param requests List of prediction requests
     * @return List of prediction responses
     */
    public java.util.List<PredictionResponse> predictBatch(java.util.List<PredictionRequest> requests) throws IOException {
        logger.info("Making batch ML predictions for {} flights", requests.size());
        String jsonBody = objectMapper.writeValueAsString(requests);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, JSON);
        
        okhttp3.Request request = buildRequest("/predictions/predict/batch")
                .post(body)
                .build();
        
        try (okhttp3.Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                logger.error("API Error {}: {}", response.code(), errorBody);
                throw new IOException("API Error " + response.code() + ": " + errorBody);
            }
            
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, 
                new com.fasterxml.jackson.core.type.TypeReference<java.util.List<PredictionResponse>>() {});
        }
    }
    
    /**
     * Make batch ML predictions using Map data (for UI)
     * @param flightDataList List of flight data maps
     * @return Response map with predictions
     */
    public Map<String, Object> batchPredict(java.util.List<Map<String, Object>> flightDataList) throws IOException {
        logger.info("Making batch ML predictions for {} flights", flightDataList.size());
        String jsonBody = objectMapper.writeValueAsString(Map.of("flights", flightDataList));
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, JSON);
        
        okhttp3.Request request = buildRequest("/predictions/predict/batch")
                .post(body)
                .build();
        
        try (okhttp3.Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                logger.error("API Error {}: {}", response.code(), errorBody);
                throw new IOException("API Error " + response.code() + ": " + errorBody);
            }
            
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, Map.class);
        }
    }
}
