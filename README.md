# UbuntuAirLab - Système de Gestion des Équipements au Sol Aéroportuaires

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://adoptium.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.1-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Application de bureau JavaFX complète pour la gestion des opérations au sol aéroportuaires, incluant le suivi des vols en temps réel, la gestion intelligente du stationnement, les prédictions basées sur l'IA et les alertes automatisées.

## 🚀 Fonctionnalités

- **Suivi des Vols en Temps Réel**: Intégration avec OpenSky Network pour les positions d'avions en direct
- **Gestion Intelligente du Stationnement**: 18 postes (N1-N2, P1-P5, S1-S10B) avec assignation automatique
- **Prédictions ML**: Prévision des retards, analyse de congestion et estimation de la durée des vols
- **Alertes Intelligentes**: Notifications en temps réel pour les événements critiques
- **Planification**: Outils avancés pour la gestion des vols et des ressources
- **Visualisation 3D**: Vue interactive de l'aéroport avec mises à jour en temps réel
- **Intégration API**: 36 endpoints entièrement intégrés avec l'API UbuntuAirLab
- **Traitement par Lots**: Prédictions massives et synchronisation automatique des données

## 📋 Prérequis

- **Java Development Kit (JDK)**: 17 ou supérieur
  - Téléchargement: [Eclipse Temurin (Adoptium)](https://adoptium.net/)
- **Apache Maven**: 3.6 ou supérieur
  - Installation: [Guide d'installation Maven](https://maven.apache.org/install.html)
- **Connexion Internet**: Requise pour l'accès API et les données en temps réel

## 🛠️ Installation et Configuration

### 1. Cloner le Dépôt

```bash
git clone https://github.com/UbuntuairLab/ubuntuairlab-frontend.git
cd ubuntuairlab-frontend
```

### 2. Compiler le Projet

```bash
mvn clean install
```

### 3. Lancer l'Application

```bash
mvn javafx:run
```

## 📦 Créer un Exécutable

Pour créer un JAR exécutable portable :

```bash
# Nettoyer et compiler
mvn clean package -DskipTests

# Le JAR sera créé dans : target/ubuntu-air-lab-3.0.0.jar
```

### Exécuter le JAR

```bash
java -Xmx1024m -Dfile.encoding=UTF-8 -jar target/ubuntu-air-lab-3.0.0.jar
```

## 🏗️ Structure du Projet

```
ubuntuairlab-frontend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/aige/apronsmart/
│       │       ├── AigApronSmartApplication.java    # Point d'entrée principal
│       │       ├── controllers/                      # Contrôleurs UI
│       │       │   ├── LoginController.java
│       │       │   ├── DashboardController.java
│       │       │   ├── RegisterController.java
│       │       │   └── modules/                      # Contrôleurs de modules
│       │       │       ├── AlertsController.java
│       │       │       ├── HistoryController.java
│       │       │       ├── NotificationsController.java
│       │       │       ├── PlanningController.java
│       │       │       ├── PostesController.java     # Gestion du stationnement
│       │       │       ├── PredictionsController.java
│       │       │       ├── RadarController.java      # Suivi temps réel
│       │       │       └── Visualization3dController.java
│       │       ├── models/                           # Modèles de données
│       │       │   ├── Alert.java
│       │       │   ├── Flight.java
│       │       │   ├── ParkingAllocation.java
│       │       │   ├── ParkingSpot.java
│       │       │   ├── User.java
│       │       │   └── ...
│       │       ├── services/                         # Logique métier
│       │       │   ├── BaseApiService.java           # Client HTTP de base
│       │       │   ├── AuthService.java
│       │       │   ├── FlightService.java
│       │       │   ├── ParkingService.java
│       │       │   ├── PredictionService.java
│       │       │   ├── AlertService.java
│       │       │   ├── OpenSkyService.java           # Suivi des vols
│       │       │   └── ...
│       │       └── utils/                            # Utilitaires
│       │           ├── Constants.java
│       │           ├── DateUtils.java
│       │           └── DialogUtils.java
│       └── resources/
│           ├── application.properties                # Configuration
│           ├── logback.xml                          # Configuration logs
│           ├── fxml/                                # Layouts UI
│           │   ├── login.fxml
│           │   ├── dashboard.fxml
│           │   ├── register.fxml
│           │   └── modules/
│           ├── css/
│           │   └── main.css                         # Styles
│           ├── html/
│           │   └── visualization3d.html             # Vue 3D
│           └── images/                              # Icônes & images
├── pom.xml                                          # Configuration Maven
└── README.md
```

## 🔧 Configuration

### Configuration de l'API

Éditer `src/main/resources/application.properties` :

```properties
# Configuration API
api.base.url=https://air-lab.bestwebapp.tech/api/v1
api.timeout=30000

# Logs
logging.level=INFO

# Application
app.name=UbuntuAirLab
app.version=3.0.0
```

### Identifiants par Défaut

```
Nom d'utilisateur : admin
Mot de passe : admin123
```

## 🎯 Composants Principaux

### 1. Système d'Authentification (`AuthService`)

Gère la connexion, l'inscription et la gestion des sessions avec tokens JWT.

```java
AuthService authService = AuthService.getInstance();
AuthResponse response = authService.login("admin", "admin123");
```

### 2. Gestion des Vols (`FlightService`)

Fournit l'accès aux données de vol avec filtrage et pagination.

```java
FlightService flightService = FlightService.getInstance();
FlightsResponse flights = flightService.getFlights("active", "arrival", 50, 0, null);
```

### 3. Gestion du Stationnement (`ParkingService`)

Gère les allocations de postes avec mapping automatique des codes (P2 → poste API "2").

```java
ParkingService parkingService = ParkingService.getInstance();
Map<String, Object> result = parkingService.assignParking("icao24", "P2");
```

### 4. Suivi Temps Réel (`OpenSkyService`)

Intégration avec OpenSky Network pour les positions d'avions en direct.

```java
OpenSkyService openSkyService = OpenSkyService.getInstance();
List<Flight> liveFlights = openSkyService.getFlightsNearAirport(6.1656, 1.2544, 300.0);
```

### 5. Prédictions ML (`PredictionService`)

Fournit les prévisions de retards, analyses de congestion et estimations de durée.

```java
PredictionService predictionService = PredictionService.getInstance();
Map<String, Object> delayPrediction = predictionService.predictDelay("icao24");
```

### 6. Système d'Alertes (`AlertService`)

Gère les alertes avec filtrage, accusé de réception et opérations par lots.

```java
AlertService alertService = AlertService.getInstance();
List<Alert> criticalAlerts = alertService.getAlerts("high", null, 100, 0);
```

## 📡 Intégration API

L'application intègre 36 endpoints de l'API UbuntuAirLab :

### Authentification (3 endpoints)
- POST `/auth/login` - Connexion utilisateur
- POST `/auth/register` - Inscription utilisateur
- POST `/auth/logout` - Déconnexion utilisateur

### Vols (9 endpoints)
- GET `/flights` - Liste de tous les vols avec filtres
- GET `/flights/{icao24}` - Détails d'un vol
- POST `/flights` - Créer un nouveau vol
- PUT `/flights/{icao24}` - Mettre à jour un vol
- DELETE `/flights/{icao24}` - Supprimer un vol
- GET `/flights/arrivals` - Obtenir les arrivées
- GET `/flights/departures` - Obtenir les départs
- GET `/flights/history` - Historique des vols
- GET `/flights/future` - Vols programmés

### Stationnement (9 endpoints)
- GET `/parking/spots` - Liste de tous les postes
- GET `/parking/spots/{id}` - Détails d'un poste
- POST `/parking/spots` - Créer un poste
- PUT `/parking/spots/{id}` - Mettre à jour un poste
- DELETE `/parking/spots/{id}` - Supprimer un poste
- GET `/parking/availability` - Vérifier la disponibilité
- GET `/parking/allocations` - Allocations actuelles
- POST `/parking/assign` - Assigner un poste à un vol
- POST `/parking/release` - Libérer un poste

### Prédictions (5 endpoints)
- POST `/predictions/delay` - Prédire un retard
- POST `/predictions/congestion` - Prédire la congestion
- POST `/predictions/duration` - Prédire la durée
- POST `/predictions/batch` - Prédictions par lots
- GET `/predictions/models` - Modèles ML disponibles

### Alertes (5 endpoints)
- GET `/alerts` - Liste de toutes les alertes
- GET `/alerts/{id}` - Détails d'une alerte
- POST `/alerts/acknowledge/{id}` - Accuser réception
- POST `/alerts/batch-acknowledge` - Accusé par lots
- GET `/alerts/statistics` - Statistiques des alertes

### Administration (3 endpoints)
- GET `/admin/users` - Liste de tous les utilisateurs
- GET `/admin/logs` - Journaux système
- GET `/admin/metrics` - Métriques système

### Synchronisation & Tableau de Bord (2 endpoints)
- POST `/sync/flights` - Synchroniser les données de vol
- GET `/dashboard/stats` - Statistiques du tableau de bord

## 🔍 Fonctionnalités Détaillées

### Mapping des Codes de Postes

Le système utilise un mapping bidirectionnel entre les codes UI et les codes API numériques :

```java
// Code UI → Code API
P1 → "1" (Cargo)
P2 → "2" (Cargo)
P4 → "4" (Cargo)
S1 → "5" (Passagers)

// Exemple : Assigner un vol au poste P2
parkingService.assignParking("ABC123", "P2");
// Convertit automatiquement P2 → "2" pour l'appel API
```

### Mises à Jour en Temps Réel

Le module radar s'actualise automatiquement toutes les 10 secondes en fusionnant :
1. Les vols de l'API locale
2. Les positions en direct du réseau OpenSky

```java
// Dans RadarController.java
private void enableAutoRefresh() {
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.seconds(10), e -> loadFlights())
    );
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
}
```

### Flux de Prédictions ML

```java
// Prédiction unique
Map<String, Object> result = predictionService.predictDelay("3c6444");

// Prédictions par lots (jusqu'à 50 vols)
List<String> icao24List = Arrays.asList("3c6444", "abc123", "def456");
Map<String, Object> batchResult = predictionService.batchPredict(icao24List);
```

## 🐛 Dépannage

### Problème : Composants JavaFX manquants

**Solution** : Assurez-vous que Java 17+ est installé. JavaFX est inclus dans le JAR.

### Problème : Erreurs de connexion / API

**Solution** :
- Vérifier la connexion Internet
- Vérifier l'endpoint API : `https://air-lab.bestwebapp.tech/api/v1`
- Vérifier les paramètres du pare-feu

### Problème : Erreurs de compilation

**Solution** : Nettoyer et recompiler :
```bash
mvn clean compile
```

### Problème : Les assignations de stationnement ne mettent pas à jour l'UI

**Solution** : C'est une limitation connue de l'API. Assurez-vous que :
1. Le vol existe dans le système (vérifier dans le module Radar Live)
2. Utilisez le format ICAO24 correct (6 caractères hexadécimaux)
3. Le code du poste correspond : P1, P2, P4 ou S1

## 🧪 Tests

Exécuter les tests :
```bash
mvn test
```

Test d'intégration (nécessite l'accès à l'API) :
```bash
mvn test -Dtest=ApiIntegrationTest
```

## 📊 Performances

- **Taille du JAR** : 58MB (toutes dépendances incluses)
- **Mémoire** : 1GB recommandé (-Xmx1024m)
- **Temps de démarrage** : ~3-5 secondes
- **Réponse API** : Moyenne 200-500ms

## 🤝 Contribution

1. Fork le dépôt
2. Créer une branche : `git checkout -b feature/fonctionnalite-incroyable`
3. Commit les changements : `git commit -m 'Ajout fonctionnalité incroyable'`
4. Push vers la branche : `git push origin feature/fonctionnalite-incroyable`
5. Ouvrir une Pull Request

## 📝 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👥 Auteurs

- **Équipe AIGE** - *Travail initial* - [UbuntuairLab](https://github.com/UbuntuairLab)

## 🙏 Remerciements

- [OpenJFX](https://openjfx.io/) - Framework JavaFX
- [OkHttp](https://square.github.io/okhttp/) - Client HTTP
- [Jackson](https://github.com/FasterXML/jackson) - Traitement JSON
- [OpenSky Network](https://opensky-network.org/) - Données de vol en temps réel
- [ControlsFX](https://controlsfx.github.io/) - Contrôles UI améliorés

## 📞 Support

Pour les problèmes et questions :
- GitHub Issues : [Signaler un bug](https://github.com/UbuntuairLab/ubuntuairlab-frontend/issues)
- Email : support@ubuntuairlab.com

## 🔗 Liens

- [Documentation API](https://air-lab.bestwebapp.tech/docs)
- [API OpenSky Network](https://openskynetwork.github.io/opensky-api/)
- [Documentation JavaFX](https://openjfx.io/javadoc/21/)

---

**Développé avec ❤️ par l'Équipe AIGE**
