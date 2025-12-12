# 🔌 Intégration API UbuntuAirLab - Guide de Référence

## ✅ Résumé de l'intégration

L'application JavaFX est maintenant connectée à l'API UbuntuAirLab en production avec **TOUTES** les fonctionnalités.

### 🌐 URL de l'API
- **Production:** `https://air-lab.bestwebapp.tech/api/v1`
- **Documentation Swagger:** `https://air-lab.bestwebapp.tech/docs`
- **OpenAPI Schema:** `https://air-lab.bestwebapp.tech/api/v1/openapi.json`
- Configurable dans: `src/main/resources/application.properties`

---

## 📦 Nouveaux Services Créés

### 1. **AuthService** - Authentification
```java
AuthService authService = AuthService.getInstance();

// Connexion
AuthResponse response = authService.login("email@example.com", "password");

// Inscription
AuthResponse response = authService.register("email", "password", "John Doe", "operator");

// Profil utilisateur
User user = authService.getCurrentUserProfile();

// Déconnexion
authService.logout();
```

### 2. **FlightService** - Gestion des vols
```java
FlightService flightService = FlightService.getInstance();

// Tous les vols avec filtres
FlightsResponse response = flightService.getFlights("active", "arrival", 50, 0);

// Vols actifs uniquement
List<Flight> activeFlights = flightService.getActiveFlights();

// Arrivées
List<Flight> arrivals = flightService.getArrivals();

// Départs
List<Flight> departures = flightService.getDepartures();

// Détails d'un vol
Flight flight = flightService.getFlightByIcao24("abc123");

// Prédictions ML pour un vol
Map<String, Object> predictions = flightService.getFlightPredictions("abc123");
```

### 3. **ParkingService** - Gestion des parkings
```java
ParkingService parkingService = ParkingService.getInstance();

// Liste des places
List<ParkingSpot> spots = parkingService.getAllParkingSpots();

// Disponibilité en temps réel
ParkingAvailability availability = parkingService.getParkingAvailability();

// Allocations actuelles
List<ParkingAllocation> allocations = parkingService.getAllocations();

// Conflits détectés
List<Map> conflicts = parkingService.getConflicts();

// Allocation automatique
Map<String, Object> result = parkingService.allocateParking("abc123", "commercial");
```

### 4. **PredictionService** - Prédictions ML
```java
PredictionService predictionService = PredictionService.getInstance();

// Créer une requête de prédiction
PredictionRequest request = new PredictionRequest();
request.setCallsign("AF1234");
request.setIcao24("abc123");
request.setVitesseActuelle(250.0);
request.setAltitude(1000.0);
// ... autres paramètres

// Obtenir prédictions
PredictionResponse prediction = predictionService.predict(request);

// Modèle 1 - ETA ajusté
double etaAjuste = prediction.getModel1Eta().getEtaAjuste();
double probaDelay15 = prediction.getModel1Eta().getProbaDelay15();

// Modèle 2 - Temps d'occupation
double tempsOccupation = prediction.getModel2Occupation().getTempsOccupationMinutes();

// Modèle 3 - Détection de conflits
boolean conflitDetecte = prediction.getModel3Conflict().isConflitDetecte();
List<String> parkingsRecommandes = prediction.getModel3Conflict().getEmplacementsRecommandes();

// Santé ML API
Map<String, Object> health = predictionService.checkHealth();
```

### 5. **DashboardService** - Statistiques
```java
DashboardService dashboardService = DashboardService.getInstance();

// Statistiques temps réel
DashboardStats stats = dashboardService.getStats();

System.out.println("Vols actifs: " + stats.getActiveFlights());
System.out.println("Arrivées aujourd'hui: " + stats.getArrivalsToday());
System.out.println("Départs aujourd'hui: " + stats.getDeparturesToday());
System.out.println("Taux d'occupation parking: " + stats.getParkingUtilization());
System.out.println("Turnaround moyen: " + stats.getAverageTurnaround() + " min");
System.out.println("Retards: " + stats.getDelaysCount());
System.out.println("Conflits détectés: " + stats.getConflictsDetected());
```

### 6. **NotificationService** - Notifications
```java
NotificationService notificationService = NotificationService.getInstance();

// Toutes les notifications
List<Map<String, Object>> notifications = notificationService.getAllNotifications();

// Notifications critiques uniquement
List<Map<String, Object>> critical = notificationService.getCriticalNotifications();

// Nombre de notifications non lues
Map<String, Object> unreadCount = notificationService.getUnreadCount();
System.out.println("Non lues: " + unreadCount.get("unread_count"));

// Acquitter une notification
Map<String, Object> result = notificationService.acknowledgeNotification("notif-123");
```

### 7. **SyncService** - Synchronisation
```java
SyncService syncService = SyncService.getInstance();

// Déclencher synchronisation manuelle
Map<String, Object> syncResult = syncService.triggerSync();

// Statut de la synchronisation
Map<String, Object> status = syncService.getSyncStatus();
System.out.println("Dernière sync: " + status.get("last_sync"));
System.out.println("Prochaine sync: " + status.get("next_sync"));

// Définir l'intervalle de sync (en minutes)
Map<String, Object> config = syncService.setSyncInterval(5);
```

---

## 🔑 Authentification

### Format de connexion
L'API utilise **OAuth2 Password Flow** avec `application/x-www-form-urlencoded`:

```
username=email@example.com&password=motdepasse
```

### Token JWT
- Le token est automatiquement stocké dans `BaseApiService.authToken`
- Ajouté automatiquement à toutes les requêtes: `Authorization: Bearer TOKEN`
- Durée de vie: **24 heures**

### Mode DEMO
Pour tester sans backend:
- Username: `demo`
- Password: `demo`

---

## 🆕 Nouveaux Modèles

### AuthResponse
```java
{
  "access_token": "eyJhbG...",
  "token_type": "bearer",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "full_name": "John Doe",
    "role": "operator",
    "is_active": true,
    "created_at": "2025-12-12T10:00:00"
  }
}
```

### FlightsResponse
```java
{
  "flights": [...],
  "total": 150,
  "limit": 50,
  "offset": 0
}
```

### ParkingAvailability
```java
{
  "total_spots": 18,
  "available": 10,
  "occupied": 7,
  "maintenance": 1,
  "utilization_rate": 0.61,
  "spots_by_type": {
    "commercial": {"total": 10, "available": 5},
    "cargo": {"total": 5, "available": 3},
    "military": {"total": 3, "available": 2}
  }
}
```

### DashboardStats
```java
{
  "active_flights": 12,
  "arrivals_today": 45,
  "departures_today": 42,
  "parking_utilization": 0.72,
  "average_turnaround": 55.3,
  "delays_count": 3,
  "conflicts_detected": 1
}
```

---

## 🔧 Modifications des Contrôleurs

### LoginController
- Utilise maintenant `authService.login(email, password)`
- Gère les erreurs 401 (identifiants incorrects)
- Limite à 3 tentatives de connexion

### DashboardController
- Affiche le rôle utilisateur depuis `user.getRole()` (String)
- Compatible avec les nouveaux formats API

### PlanningController
- Utilise `flightService.getAllFlights()` au lieu de `getFlightsByDate()`
- Note: Le filtrage par date peut être ajouté côté client si nécessaire

---

## ⚙️ Configuration

### application.properties
```properties
# API URL (Production)
api.base.url=https://air-lab.bestwebapp.tech/api/v1

# Pour basculer en développement local:
# api.base.url=http://localhost:8080/api/v1
```

### BaseApiService
- Timeout: 30 secondes
- Auto-retry sur 401 → logout automatique
- Support form-urlencoded ET JSON
- Logging des erreurs API

---

## 🧪 Test de l'intégration

### 1. Compiler
```bash
mvn clean compile
```

### 2. Lancer l'application
```bash
mvn javafx:run
```

### 3. Se connecter
- Mode DEMO: `demo` / `demo`
- Compte réel: Créer via `/auth/register` ou utiliser compte existant

### 4. Tester les modules
- **Dashboard**: Vérifier les statistiques en temps réel
- **Radar Live**: Voir les vols actifs sur la carte
- **Planning**: Voir les vols planifiés
- **Postes**: Vérifier la disponibilité des 18 parkings
- **Alertes**: Consulter les conflits détectés

---

## 🐛 Gestion des Erreurs

### Codes HTTP
| Code | Signification | Action |
|------|---------------|--------|
| 200 | Succès | ✅ |
| 401 | Non authentifié | Redirection login |
| 403 | Permissions insuffisantes | Message d'erreur |
| 404 | Ressource non trouvée | Vérifier ID |
| 500 | Erreur serveur | Réessayer |
| 503 | ML API offline | Désactiver prédictions |

### Logging
Tous les appels API sont loggés dans:
```
logs/application.log
```

---

## 📊 Endpoints Disponibles (COMPLET)

### Authentification
- `POST /auth/login` - Connexion (OAuth2)
- `GET /auth/me` - Profil utilisateur

### Vols
- `GET /flights/` - Liste des vols avec filtres
- `GET /flights/{icao24}` - Détails d'un vol
- `GET /flights/{icao24}/predictions` - Prédictions ML pour un vol

### Parking
- `GET /parking/spots` - Liste des places de parking
- `GET /parking/spots/{spot_id}` - Détails d'une place
- `GET /parking/allocations` - Allocations actuelles
- `GET /parking/allocations/{allocation_id}` - Détails d'une allocation
- `GET /parking/availability` - Disponibilité temps réel
- `GET /parking/conflicts` - Conflits détectés
- `POST /parking/allocate` - Allocation automatique
- `POST /parking/assign` - Assignation manuelle
- `POST /parking/civil-recall` - Rappel d'avion civil
- `POST /parking/military-transfer` - Transfert militaire

### Prédictions ML
- `POST /predictions/predict` - Prédiction ML unique
- `POST /predictions/predict/batch` - Prédictions ML en batch
- `GET /predictions/health` - Santé API ML
- `GET /predictions/models/info` - Info modèles ML

### Dashboard
- `GET /dashboard/stats` - Statistiques temps réel complètes

### Notifications
- `GET /notifications/notifications` - Toutes les notifications
- `GET /notifications/notifications/critical` - Notifications critiques
- `GET /notifications/notifications/unread/count` - Nombre non lues
- `POST /notifications/notifications/{id}/acknowledge` - Acquitter

### Synchronisation
- `POST /sync/trigger` - Déclencher sync manuelle
- `GET /sync/status` - Statut de la synchronisation
- `POST /sync/interval/{minutes}` - Définir l'intervalle

### Système
- `GET /` - Info API
- `GET /health` - Health check

---

## 🚀 Prochaines Étapes

### À implémenter
1. **Rafraîchissement automatique** (polling toutes les 30s)
2. **Filtrage par date** dans PlanningController
3. **Notifications** en temps réel
4. **Cache local** pour réduire les appels API
5. **Gestion du refresh token** (actuellement 24h)

### Améliorations possibles
- WebSocket pour les mises à jour temps réel
- Meilleure gestion des erreurs réseau
- Mode offline avec données en cache
- Export des données (PDF, Excel)

---

## 📚 Ressources

- **API Documentation (Swagger)**: https://air-lab.bestwebapp.tech/docs
- **OpenAPI Schema**: https://air-lab.bestwebapp.tech/openapi.json
- **ML API**: https://tagba-ubuntuairlab.hf.space/docs

---

**Version**: 1.0.0  
**Date**: 12 Décembre 2025  
**Status**: ✅ Intégration complète et fonctionnelle
