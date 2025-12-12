# UbuntuAirLab v3.0

Système intelligent de gestion des postes de stationnement aéroportuaires pour l'Aéroport International Gnassingbé Eyadéma, Lomé, Togo.

## Description

Application JavaFX pour la gestion intelligente des postes de stationnement avec 6 modules principaux :
- **Radar Live** : Visualisation en temps réel des vols
- **Visualisation 3D** : Vue 3D de l'aéroport et des postes
- **Gestion Postes** : Gestion des 18 postes de stationnement
- **Planification** : Planning des vols et affectations
- **Alertes** : Système de notifications et alertes
- **Historique** : Consultation des données historiques

## Design

L'application utilise un design moderne avec thème sombre :
- **Couleur de fond** : #111921
- **Couleur primaire** : #1773cf
- **Couleur des cartes** : #1a2632
- **Police** : Inter
- **Interface mobile-first** : 480x884px

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- JavaFX 21+

## Installation

```bash
mvn clean install
```

## Exécution

```bash
mvn javafx:run
```

## Mode Demo

Utilisez les identifiants suivants pour tester sans backend :
- **Utilisateur** : demo
- **Mot de passe** : demo

## Structure du Projet

```
src/main/java/com/aige/aproonsmart/
├── AigApronSmartApplication.java   # Main application (UbuntuAirLab)
├── controllers/                     # Controllers for each module
├── models/                          # Data models
├── services/                        # API services
├── utils/                           # Utility classes
└── views/                           # Custom views

src/main/resources/
├── fxml/                            # FXML layouts
├── css/                             # Stylesheets
└── images/                          # Images and icons
```

## Configuration

L'URL de l'API backend doit être configurée dans `src/main/resources/application.properties`:

```properties
api.base.url=http://localhost:8080/api
```

## Technologies

- JavaFX 21
- ControlsFX (enhanced controls)
- FontAwesomeFX (icons)
- OkHttp (HTTP client)
- Jackson (JSON processing)
- Logback (logging)

## Auteur

AIGE - Direction des Systèmes d'Information
