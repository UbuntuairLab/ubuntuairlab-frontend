package com.aige.apronsmart.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date/time formatting
 */
public class DateUtils {
    
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter TIME_SHORT_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "-";
    }
    
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "-";
    }
    
    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMATTER) : "-";
    }
    
    public static String formatTimeShort(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_SHORT_FORMATTER) : "-";
    }
}
