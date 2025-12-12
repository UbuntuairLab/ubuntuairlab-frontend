package com.aige.apronsmart.models;

import java.time.LocalDateTime;

/**
 * Model representing an alert/notification
 */
public class Alert {
    private Long id;
    private AlertType type;
    private AlertSeverity severity;
    private AlertStatus status;
    
    private String title;
    private String message;
    private String details;
    
    private Long relatedFlightId;
    private Long relatedPosteId;
    
    private String assignedToUser;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    
    // No-arg constructor
    public Alert() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public AlertType getType() {
        return type;
    }
    
    public void setType(AlertType type) {
        this.type = type;
    }
    
    public AlertSeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }
    
    public AlertStatus getStatus() {
        return status;
    }
    
    public void setStatus(AlertStatus status) {
        this.status = status;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public Long getRelatedFlightId() {
        return relatedFlightId;
    }
    
    public void setRelatedFlightId(Long relatedFlightId) {
        this.relatedFlightId = relatedFlightId;
    }
    
    public Long getRelatedPosteId() {
        return relatedPosteId;
    }
    
    public void setRelatedPosteId(Long relatedPosteId) {
        this.relatedPosteId = relatedPosteId;
    }
    
    public String getAssignedToUser() {
        return assignedToUser;
    }
    
    public void setAssignedToUser(String assignedToUser) {
        this.assignedToUser = assignedToUser;
    }
    
    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }
    
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }
    
    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }
    
    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public enum AlertType {
        SATURATION("Saturation"),
        CONFLICT("Conflit"),
        DELAY("Retard"),
        EMERGENCY("Urgence"),
        WEATHER("Météo"),
        TECHNICAL("Technique"),
        SECURITY("Sécurité"),
        INFO("Information");
        
        private final String displayName;
        
        AlertType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum AlertSeverity {
        LOW("Faible", "#4CAF50"),
        MEDIUM("Moyenne", "#FF9800"),
        HIGH("Élevée", "#F44336"),
        CRITICAL("Critique", "#9C27B0");
        
        private final String displayName;
        private final String colorHex;
        
        AlertSeverity(String displayName, String colorHex) {
            this.displayName = displayName;
            this.colorHex = colorHex;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getColorHex() {
            return colorHex;
        }
    }
    
    public enum AlertStatus {
        NEW("Nouvelle"),
        ACKNOWLEDGED("Reconnue"),
        IN_PROGRESS("En Cours"),
        RESOLVED("Résolue"),
        IGNORED("Ignorée");
        
        private final String displayName;
        
        AlertStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
