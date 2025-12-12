# AIGE-APRON-SMART v3.0 - Guide de Déploiement

## Prérequis

### Système d'exploitation
- Windows 10/11, macOS 10.14+, ou Linux (Ubuntu 20.04+)

### Logiciels requis
- **Java JDK 17 ou supérieur** : [Télécharger](https://adoptium.net/)
- **Maven 3.6+** : [Télécharger](https://maven.apache.org/download.cgi)
- **Git** (optionnel) : [Télécharger](https://git-scm.com/)

### Vérification de l'installation

```bash
java -version    # Devrait afficher Java 17 ou supérieur
mvn -version     # Devrait afficher Maven 3.6 ou supérieur
```

## Installation

### 1. Récupération du code source

```bash
# Si vous utilisez Git
git clone <repository-url>
cd frontend_anac

# Ou décompresser l'archive ZIP
unzip frontend_anac.zip
cd frontend_anac
```

### 2. Installation des dépendances

```bash
mvn clean install
```

Cette commande va :
- Télécharger toutes les dépendances nécessaires
- Compiler le code source
- Créer le fichier JAR exécutable

## Configuration

### Configuration de l'API Backend

Éditer le fichier `src/main/resources/application.properties` :

```properties
# URL de l'API backend
api.base.url=http://localhost:8080/api

# Pour un serveur de production
# api.base.url=https://api.aige-apron.tg/api
```

### Configuration du logging

Le niveau de logging peut être ajusté dans `src/main/resources/logback.xml`

## Exécution

### Mode développement

```bash
# Linux/Mac
./run.sh

# Windows
mvn javafx:run
```

### Compilation pour production

```bash
mvn clean package

# Le fichier JAR sera créé dans target/apron-smart-3.0.0.jar
```

### Exécution du JAR

```bash
java -jar target/apron-smart-3.0.0.jar
```

## Structure du Projet

```
frontend_anac/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/aige/apronsmart/
│   │   │       ├── AigApronSmartApplication.java  # Point d'entrée
│   │   │       ├── controllers/                    # Contrôleurs
│   │   │       │   ├── LoginController.java
│   │   │       │   ├── DashboardController.java
│   │   │       │   └── modules/                    # Modules spécialisés
│   │   │       │       ├── RadarController.java
│   │   │       │       ├── PostesController.java
│   │   │       │       ├── PlanningController.java
│   │   │       │       ├── AlertsController.java
│   │   │       │       ├── HistoryController.java
│   │   │       │       └── Visualization3dController.java
│   │   │       ├── models/                         # Modèles de données
│   │   │       │   ├── Flight.java
│   │   │       │   ├── Poste.java
│   │   │       │   ├── Alert.java
│   │   │       │   └── User.java
│   │   │       ├── services/                       # Services API
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── FlightService.java
│   │   │       │   ├── PosteService.java
│   │   │       │   └── AlertService.java
│   │   │       └── utils/                          # Utilitaires
│   │   │           ├── Constants.java
│   │   │           ├── DateUtils.java
│   │   │           └── DialogUtils.java
│   │   └── resources/
│   │       ├── fxml/                               # Interfaces FXML
│   │       │   ├── login.fxml
│   │       │   ├── dashboard.fxml
│   │       │   └── modules/
│   │       │       ├── radar.fxml
│   │       │       ├── postes.fxml
│   │       │       ├── planning.fxml
│   │       │       ├── alerts.fxml
│   │       │       ├── history.fxml
│   │       │       └── visualization3d.fxml
│   │       ├── css/
│   │       │   └── main.css                        # Styles CSS
│   │       ├── images/                             # Images et icônes
│   │       │   └── logo.png
│   │       ├── application.properties              # Configuration
│   │       └── logback.xml                         # Configuration logging
├── maquette/                                       # Maquettes UI
├── pom.xml                                         # Configuration Maven
├── README.md                                       # Documentation
└── run.sh                                          # Script de lancement
```

## Modules de l'Application

### 1. Module d'Authentification
- Connexion sécurisée avec Azure AD
- Gestion de session
- 3 tentatives de connexion

### 2. Dashboard Principal
- Vue d'ensemble en temps réel
- Navigation entre modules
- Horloge et informations utilisateur

### 3. Module Radar Live
- Carte interactive avec Leaflet/OpenStreetMap
- Visualisation des vols en temps réel
- Filtrage par rayon, altitude, compagnie
- Auto-refresh toutes les 5 secondes

### 4. Module Visualisation 3D
- Vue 3D de l'aéroport avec Three.js
- Postes colorés selon statut
- Modes : Normal, Prédiction IA, Planification, Replay
- Contrôles caméra interactifs

### 5. Module Gestion des Postes
- Grille de 18 postes de stationnement
- Codes couleur : Vert (Libre), Rouge (Occupé), Orange (Réservé), Bleu (Militaire)
- Édition des attributs (Admin/Ops uniquement)
- Taux d'occupation en temps réel

### 6. Module Planification
- Calendrier jour/semaine/mois
- Création et modification de vols
- Suggestions IA
- Export CSV/PDF/iCal

### 7. Module Alertes
- Gestion des alertes par criticité
- Types : Saturation, Conflit, Retard, Urgence, Météo
- Actions : Reconnaître, Résoudre, Ignorer

### 8. Module Historique
- Recherche avancée avec filtres
- Export de rapports
- Analyses statistiques

## API Endpoints Attendus

Le frontend attend que le backend expose les endpoints suivants :

### Authentification
- `POST /api/auth/login` - Connexion utilisateur

### Vols
- `GET /api/flights` - Liste de tous les vols
- `GET /api/flights/active` - Vols actifs
- `GET /api/flights/{id}` - Détails d'un vol
- `POST /api/flights` - Créer un vol
- `PUT /api/flights/{id}` - Modifier un vol
- `DELETE /api/flights/{id}` - Supprimer un vol
- `POST /api/flights/{flightId}/assign-poste/{posteId}` - Attribuer un poste

### Postes
- `GET /api/postes` - Liste de tous les postes
- `GET /api/postes/available` - Postes disponibles
- `GET /api/postes/{id}` - Détails d'un poste
- `POST /api/postes` - Créer un poste
- `PUT /api/postes/{id}` - Modifier un poste
- `DELETE /api/postes/{id}` - Supprimer un poste
- `POST /api/postes/{id}/release` - Libérer un poste
- `GET /api/postes/occupation-rate` - Taux d'occupation

### Alertes
- `GET /api/alerts` - Liste de toutes les alertes
- `GET /api/alerts/active` - Alertes actives
- `GET /api/alerts/{id}` - Détails d'une alerte
- `POST /api/alerts/{id}/acknowledge` - Reconnaître une alerte
- `POST /api/alerts/{id}/resolve` - Résoudre une alerte
- `DELETE /api/alerts/{id}` - Supprimer une alerte

## Dépannage

### Erreur : "Java version is too old"
- Installer Java 17 ou supérieur
- Vérifier la variable d'environnement JAVA_HOME

### Erreur : "Cannot connect to backend API"
- Vérifier que l'API backend est démarrée
- Vérifier l'URL dans `application.properties`
- Vérifier le pare-feu/proxy

### Erreur : "Module not found"
- Exécuter `mvn clean install` pour télécharger les dépendances

### Interface ne s'affiche pas correctement
- Vérifier que JavaFX est installé
- Essayer avec une autre version de Java

## Support

Pour toute question ou problème :
- Email : support@aige.tg
- Documentation : [Wiki interne]
- Issues : [Système de tickets]

## Licence

© AIGE - Direction des Systèmes d'Information
Aéroport International Gnassingbé Eyadéma, Lomé, Togo
