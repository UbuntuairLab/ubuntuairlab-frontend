package com.aige.apronsmart.services;

import com.aige.apronsmart.models.AuthResponse;
import com.aige.apronsmart.models.DashboardStats;
import com.aige.apronsmart.models.FlightsResponse;
import com.aige.apronsmart.models.ParkingAvailability;

import java.util.List;
import java.util.Map;

/**
 * Test class to verify API integration
 * Run this to test connection to UbuntuAirLab API
 */
public class ApiIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing UbuntuAirLab API Integration ===\n");
        
        // Test 1: API Health Check
        testApiHealth();
        
        // Test 2: Authentication (will fail without valid credentials, that's OK)
        testAuthentication();
        
        // Test 3: ML API Health
        testMLHealth();
        
        System.out.println("\n=== Tests Complete ===");
        System.out.println("Note: Some tests may fail if not authenticated.");
        System.out.println("To fully test, login first with valid credentials.");
    }
    
    private static void testApiHealth() {
        System.out.println("1. Testing API Health...");
        try {
            SyncService syncService = SyncService.getInstance();
            Map<String, Object> status = syncService.getSyncStatus();
            System.out.println("   ✓ API is reachable");
            System.out.println("   Sync status: " + status);
        } catch (Exception e) {
            System.out.println("   ✗ API health check failed: " + e.getMessage());
        }
    }
    
    private static void testAuthentication() {
        System.out.println("\n2. Testing Authentication...");
        try {
            AuthService authService = AuthService.getInstance();
            // This will fail with invalid credentials, but it tests the endpoint
            AuthResponse response = authService.login("test@test.com", "wrongpassword");
            System.out.println("   ✓ Auth endpoint is working");
        } catch (Exception e) {
            if (e.getMessage().contains("401") || e.getMessage().contains("username or password")) {
                System.out.println("   ✓ Auth endpoint is working (credentials rejected as expected)");
            } else {
                System.out.println("   ✗ Auth test failed: " + e.getMessage());
            }
        }
    }
    
    private static void testMLHealth() {
        System.out.println("\n3. Testing ML API Health...");
        try {
            PredictionService predictionService = PredictionService.getInstance();
            Map<String, Object> health = predictionService.checkHealth();
            System.out.println("   ✓ ML API is reachable");
            System.out.println("   ML Health: " + health);
        } catch (Exception e) {
            System.out.println("   ✗ ML API health check failed: " + e.getMessage());
        }
    }
    
    /**
     * Test with authenticated user
     * Call this after successful login
     */
    public static void testAuthenticatedEndpoints() {
        System.out.println("\n=== Testing Authenticated Endpoints ===\n");
        
        // Test Flights
        System.out.println("1. Testing Flights endpoint...");
        try {
            FlightService flightService = FlightService.getInstance();
            FlightsResponse response = flightService.getFlights("active", null, 10, 0, null);
            System.out.println("   ✓ Flights: " + response.getTotal() + " total, " + 
                             response.getFlights().size() + " returned");
        } catch (Exception e) {
            System.out.println("   ✗ Flights test failed: " + e.getMessage());
        }
        
        // Test Parking
        System.out.println("\n2. Testing Parking endpoint...");
        try {
            ParkingService parkingService = ParkingService.getInstance();
            ParkingAvailability availability = parkingService.getAvailability();
            System.out.println("   ✓ Parking: " + availability.getAvailable() + "/" + 
                             availability.getTotalSpots() + " available");
        } catch (Exception e) {
            System.out.println("   ✗ Parking test failed: " + e.getMessage());
        }
        
        // Test Dashboard
        System.out.println("\n3. Testing Dashboard endpoint...");
        try {
            DashboardService dashboardService = DashboardService.getInstance();
            DashboardStats stats = dashboardService.getStats();
            System.out.println("   ✓ Dashboard: " + stats.getActiveFlights() + " active flights, " +
                             stats.getConflictsDetected() + " conflicts");
        } catch (Exception e) {
            System.out.println("   ✗ Dashboard test failed: " + e.getMessage());
        }
        
        // Test Notifications
        System.out.println("\n4. Testing Notifications endpoint...");
        try {
            NotificationService notificationService = NotificationService.getInstance();
            Map<String, Object> unreadCount = notificationService.getUnreadCount();
            System.out.println("   ✓ Notifications: " + unreadCount.get("unread_count") + " unread");
        } catch (Exception e) {
            System.out.println("   ✗ Notifications test failed: " + e.getMessage());
        }
        
        System.out.println("\n=== All Authenticated Tests Complete ===");
    }
}
