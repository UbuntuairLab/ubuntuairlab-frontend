package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Request and response models for ML predictions
 */
public class PredictionModels {
    
    /**
     * Request model for flight prediction
     */
    public static class PredictionRequest {
        private String callsign;
        private String icao24;
        
        @JsonProperty("vitesse_actuelle")
        private double vitesseActuelle;
        
        private double altitude;
        
        @JsonProperty("distance_piste")
        private double distancePiste;
        
        private double temperature;
        
        @JsonProperty("vent_vitesse")
        private double ventVitesse;
        
        @JsonProperty("vent_direction")
        private double ventDirection;
        
        private double visibilite;
        private double pluie;
        private String compagnie;
        
        @JsonProperty("retard_historique_compagnie")
        private double retardHistoriqueCompagnie;
        
        @JsonProperty("trafic_approche")
        private int traficApproche;
        
        @JsonProperty("occupation_tarmac")
        private double occupationTarmac;
        
        @JsonProperty("type_avion")
        private String typeAvion;
        
        @JsonProperty("historique_occupation_avion")
        private double historiqueOccupationAvion;
        
        @JsonProperty("type_vol")
        private int typeVol;
        
        @JsonProperty("passagers_estimes")
        private int passagersEstimes;
        
        @JsonProperty("disponibilite_emplacements")
        private int disponibiliteEmplacements;
        
        @JsonProperty("occupation_actuelle")
        private double occupationActuelle;
        
        @JsonProperty("meteo_score")
        private double meteoScore;
        
        @JsonProperty("trafic_entrant")
        private int traficEntrant;
        
        @JsonProperty("trafic_sortant")
        private int traficSortant;
        
        @JsonProperty("priorite_vol")
        private int prioriteVol;
        
        @JsonProperty("emplacements_futurs_libres")
        private int emplacementsFutursLibres;
        
        @JsonProperty("heure_jour")
        private int heureJour;
        
        @JsonProperty("jour_semaine")
        private int jourSemaine;
        
        @JsonProperty("periode_annee")
        private int periodeAnnee;
        
        // Getters and setters
        public String getCallsign() { return callsign; }
        public void setCallsign(String callsign) { this.callsign = callsign; }
        
        public String getIcao24() { return icao24; }
        public void setIcao24(String icao24) { this.icao24 = icao24; }
        
        public double getVitesseActuelle() { return vitesseActuelle; }
        public void setVitesseActuelle(double vitesseActuelle) { this.vitesseActuelle = vitesseActuelle; }
        
        public double getAltitude() { return altitude; }
        public void setAltitude(double altitude) { this.altitude = altitude; }
        
        public double getDistancePiste() { return distancePiste; }
        public void setDistancePiste(double distancePiste) { this.distancePiste = distancePiste; }
        
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        
        public double getVentVitesse() { return ventVitesse; }
        public void setVentVitesse(double ventVitesse) { this.ventVitesse = ventVitesse; }
        
        public double getVentDirection() { return ventDirection; }
        public void setVentDirection(double ventDirection) { this.ventDirection = ventDirection; }
        
        public double getVisibilite() { return visibilite; }
        public void setVisibilite(double visibilite) { this.visibilite = visibilite; }
        
        public double getPluie() { return pluie; }
        public void setPluie(double pluie) { this.pluie = pluie; }
        
        public String getCompagnie() { return compagnie; }
        public void setCompagnie(String compagnie) { this.compagnie = compagnie; }
        
        public double getRetardHistoriqueCompagnie() { return retardHistoriqueCompagnie; }
        public void setRetardHistoriqueCompagnie(double retardHistoriqueCompagnie) { this.retardHistoriqueCompagnie = retardHistoriqueCompagnie; }
        
        public int getTraficApproche() { return traficApproche; }
        public void setTraficApproche(int traficApproche) { this.traficApproche = traficApproche; }
        
        public double getOccupationTarmac() { return occupationTarmac; }
        public void setOccupationTarmac(double occupationTarmac) { this.occupationTarmac = occupationTarmac; }
        
        public String getTypeAvion() { return typeAvion; }
        public void setTypeAvion(String typeAvion) { this.typeAvion = typeAvion; }
        
        public double getHistoriqueOccupationAvion() { return historiqueOccupationAvion; }
        public void setHistoriqueOccupationAvion(double historiqueOccupationAvion) { this.historiqueOccupationAvion = historiqueOccupationAvion; }
        
        public int getTypeVol() { return typeVol; }
        public void setTypeVol(int typeVol) { this.typeVol = typeVol; }
        
        public int getPassagersEstimes() { return passagersEstimes; }
        public void setPassagersEstimes(int passagersEstimes) { this.passagersEstimes = passagersEstimes; }
        
        public int getDisponibiliteEmplacements() { return disponibiliteEmplacements; }
        public void setDisponibiliteEmplacements(int disponibiliteEmplacements) { this.disponibiliteEmplacements = disponibiliteEmplacements; }
        
        public double getOccupationActuelle() { return occupationActuelle; }
        public void setOccupationActuelle(double occupationActuelle) { this.occupationActuelle = occupationActuelle; }
        
        public double getMeteoScore() { return meteoScore; }
        public void setMeteoScore(double meteoScore) { this.meteoScore = meteoScore; }
        
        public int getTraficEntrant() { return traficEntrant; }
        public void setTraficEntrant(int traficEntrant) { this.traficEntrant = traficEntrant; }
        
        public int getTraficSortant() { return traficSortant; }
        public void setTraficSortant(int traficSortant) { this.traficSortant = traficSortant; }
        
        public int getPrioriteVol() { return prioriteVol; }
        public void setPrioriteVol(int prioriteVol) { this.prioriteVol = prioriteVol; }
        
        public int getEmplacementsFutursLibres() { return emplacementsFutursLibres; }
        public void setEmplacementsFutursLibres(int emplacementsFutursLibres) { this.emplacementsFutursLibres = emplacementsFutursLibres; }
        
        public int getHeureJour() { return heureJour; }
        public void setHeureJour(int heureJour) { this.heureJour = heureJour; }
        
        public int getJourSemaine() { return jourSemaine; }
        public void setJourSemaine(int jourSemaine) { this.jourSemaine = jourSemaine; }
        
        public int getPeriodeAnnee() { return periodeAnnee; }
        public void setPeriodeAnnee(int periodeAnnee) { this.periodeAnnee = periodeAnnee; }
    }
    
    /**
     * Response model for ML prediction
     */
    public static class PredictionResponse {
        @JsonProperty("model_1_eta")
        private Model1Eta model1Eta;
        
        @JsonProperty("model_2_occupation")
        private Model2Occupation model2Occupation;
        
        @JsonProperty("model_3_conflict")
        private Model3Conflict model3Conflict;
        
        private Metadata metadata;
        
        public static class Model1Eta {
            @JsonProperty("eta_ajuste")
            private double etaAjuste;
            
            @JsonProperty("proba_delay_15")
            private double probaDelay15;
            
            @JsonProperty("proba_delay_30")
            private double probaDelay30;
            
            private double confidence;
            
            public double getEtaAjuste() { return etaAjuste; }
            public void setEtaAjuste(double etaAjuste) { this.etaAjuste = etaAjuste; }
            
            public double getProbaDelay15() { return probaDelay15; }
            public void setProbaDelay15(double probaDelay15) { this.probaDelay15 = probaDelay15; }
            
            public double getProbaDelay30() { return probaDelay30; }
            public void setProbaDelay30(double probaDelay30) { this.probaDelay30 = probaDelay30; }
            
            public double getConfidence() { return confidence; }
            public void setConfidence(double confidence) { this.confidence = confidence; }
        }
        
        public static class Model2Occupation {
            @JsonProperty("temps_occupation_minutes")
            private double tempsOccupationMinutes;
            
            @JsonProperty("intervalle_confiance_min")
            private double intervalleConfianceMin;
            
            @JsonProperty("intervalle_confiance_max")
            private double intervalleConfianceMax;
            
            public double getTempsOccupationMinutes() { return tempsOccupationMinutes; }
            public void setTempsOccupationMinutes(double tempsOccupationMinutes) { this.tempsOccupationMinutes = tempsOccupationMinutes; }
            
            public double getIntervalleConfianceMin() { return intervalleConfianceMin; }
            public void setIntervalleConfianceMin(double intervalleConfianceMin) { this.intervalleConfianceMin = intervalleConfianceMin; }
            
            public double getIntervalleConfianceMax() { return intervalleConfianceMax; }
            public void setIntervalleConfianceMax(double intervalleConfianceMax) { this.intervalleConfianceMax = intervalleConfianceMax; }
        }
        
        public static class Model3Conflict {
            @JsonProperty("conflit_detecte")
            private boolean conflitDetecte;
            
            @JsonProperty("score_risque")
            private double scoreRisque;
            
            @JsonProperty("emplacements_recommandes")
            private List<String> emplacementsRecommandes;
            
            public boolean isConflitDetecte() { return conflitDetecte; }
            public void setConflitDetecte(boolean conflitDetecte) { this.conflitDetecte = conflitDetecte; }
            
            public double getScoreRisque() { return scoreRisque; }
            public void setScoreRisque(double scoreRisque) { this.scoreRisque = scoreRisque; }
            
            public List<String> getEmplacementsRecommandes() { return emplacementsRecommandes; }
            public void setEmplacementsRecommandes(List<String> emplacementsRecommandes) { this.emplacementsRecommandes = emplacementsRecommandes; }
        }
        
        public static class Metadata {
            private String timestamp;
            
            @JsonProperty("model_version")
            private String modelVersion;
            
            public String getTimestamp() { return timestamp; }
            public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
            
            public String getModelVersion() { return modelVersion; }
            public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
        }
        
        public Model1Eta getModel1Eta() { return model1Eta; }
        public void setModel1Eta(Model1Eta model1Eta) { this.model1Eta = model1Eta; }
        
        public Model2Occupation getModel2Occupation() { return model2Occupation; }
        public void setModel2Occupation(Model2Occupation model2Occupation) { this.model2Occupation = model2Occupation; }
        
        public Model3Conflict getModel3Conflict() { return model3Conflict; }
        public void setModel3Conflict(Model3Conflict model3Conflict) { this.model3Conflict = model3Conflict; }
        
        public Metadata getMetadata() { return metadata; }
        public void setMetadata(Metadata metadata) { this.metadata = metadata; }
    }
}
