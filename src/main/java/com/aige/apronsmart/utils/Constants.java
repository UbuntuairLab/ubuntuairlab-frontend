package com.aige.apronsmart.utils;

/**
 * Application constants
 */
public class Constants {
    
    // Application Info
    public static final String APP_NAME = "Ubuntuairlab";
    public static final String APP_VERSION = "3.0.0";
    public static final String APP_TITLE = APP_NAME + " v" + APP_VERSION;
    
    // Airport Info
    public static final String AIRPORT_NAME = "Aéroport International Gnassingbé Eyadéma";
    public static final String AIRPORT_CODE = "LFW";
    public static final String AIRPORT_CITY = "Lomé";
    public static final String AIRPORT_COUNTRY = "Togo";
    public static final double AIRPORT_LATITUDE = 6.1656;
    public static final double AIRPORT_LONGITUDE = 1.2544;
    
    // Postes
    public static final int TOTAL_POSTES = 18;
    public static final int MILITARY_POSTES = 4;
    
    // Refresh Intervals (milliseconds)
    public static final int RADAR_REFRESH_INTERVAL = 5000;
    public static final int ALERTS_REFRESH_INTERVAL = 10000;
    public static final int POSTES_REFRESH_INTERVAL = 15000;
    
    // Map Settings
    public static final int DEFAULT_MAP_ZOOM = 11;
    public static final int DEFAULT_RADAR_RADIUS = 200; // km
}
