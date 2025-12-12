package com.aige.apronsmart.services;

import com.aige.apronsmart.models.Poste;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;

/**
 * Service for poste (parking stand) operations
 */
public class PosteService extends BaseApiService {
    
    private static PosteService instance;
    
    private PosteService() {}
    
    public static PosteService getInstance() {
        if (instance == null) {
            instance = new PosteService();
        }
        return instance;
    }
    
    public List<Poste> getAllPostes() throws IOException {
        String response = httpClient.newCall(buildRequest("/postes").get().build()).execute().body().string();
        return objectMapper.readValue(response, new TypeReference<List<Poste>>() {});
    }
    
    public List<Poste> getAvailablePostes() throws IOException {
        String response = httpClient.newCall(buildRequest("/postes/available").get().build()).execute().body().string();
        return objectMapper.readValue(response, new TypeReference<List<Poste>>() {});
    }
    
    public Poste getPosteById(Long id) throws IOException {
        return get("/postes/" + id, Poste.class);
    }
    
    public Poste createPoste(Poste poste) throws IOException {
        return post("/postes", poste, Poste.class);
    }
    
    public Poste updatePoste(Long id, Poste poste) throws IOException {
        return put("/postes/" + id, poste, Poste.class);
    }
    
    public void deletePoste(Long id) throws IOException {
        delete("/postes/" + id);
    }
    
    public Poste releasePoste(Long id) throws IOException {
        return post("/postes/" + id + "/release", null, Poste.class);
    }
    
    public Double getOccupationRate() throws IOException {
        String response = httpClient.newCall(
                buildRequest("/postes/occupation-rate").get().build()
        ).execute().body().string();
        return objectMapper.readValue(response, Double.class);
    }
}
