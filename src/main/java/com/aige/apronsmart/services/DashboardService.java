package com.aige.apronsmart.services;

import com.aige.apronsmart.models.DashboardStats;

import java.io.IOException;

/**
 * Service for dashboard statistics with UbuntuAirLab API
 */
public class DashboardService extends BaseApiService {
    
    private static DashboardService instance;
    
    private DashboardService() {}
    
    public static DashboardService getInstance() {
        if (instance == null) {
            instance = new DashboardService();
        }
        return instance;
    }
    
    /**
     * Get real-time dashboard statistics
     * @return DashboardStats with comprehensive metrics
     */
    public DashboardStats getStats() throws IOException {
        logger.info("Fetching dashboard statistics");
        return get("/dashboard/stats", DashboardStats.class);
    }
}
