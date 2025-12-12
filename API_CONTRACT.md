# AIGE-APRON-SMART v3.0 - API Contract

Ce document décrit le contrat d'API entre le frontend JavaFX et le backend REST.

## Base URL

```
http://localhost:8080/api
```

Pour la production : `https://api.aige-apron.tg/api`

## Authentification

Toutes les requêtes (sauf `/auth/login`) doivent inclure un header d'authentification :

```
Authorization: Bearer {token}
```

---

## 1. Authentification

### POST /auth/login

Authentifie un utilisateur et retourne un token JWT.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "john.doe",
    "email": "john.doe@aige.tg",
    "firstName": "John",
    "lastName": "Doe",
    "role": "ADMIN",
    "department": "Operations",
    "phone": "+228 XX XX XX XX",
    "isActive": true,
    "lastLogin": "2024-12-10T14:30:00",
    "createdAt": "2024-01-01T00:00:00"
  },
  "message": "Connexion réussie"
}
```

**Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Identifiants incorrects"
}
```

---

## 2. Vols (Flights)

### GET /flights

Récupère tous les vols.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "callsign": "AF1234",
    "aircraftType": "A320",
    "company": "Air France",
    "nature": "PAX",
    "status": "EN_ROUTE",
    "origin": "CDG",
    "destination": "LFW",
    "eta": "2024-12-10T16:30:00",
    "etd": "2024-12-10T18:00:00",
    "ata": null,
    "atd": null,
    "latitude": 6.5,
    "longitude": 1.8,
    "altitude": 10000,
    "heading": 180,
    "speed": 450,
    "passengers": 150,
    "cargoWeight": 2000,
    "assignedPosteId": null,
    "assignedPosteCode": null,
    "notes": "",
    "isEmergency": false,
    "isDelayed": false,
    "createdAt": "2024-12-10T10:00:00",
    "updatedAt": "2024-12-10T14:30:00"
  }
]
```

### GET /flights/active

Récupère uniquement les vols actifs (EN_ROUTE, APPROACHING, LANDED, PARKED).

**Response:** Même format que `/flights`

### GET /flights/{id}

Récupère les détails d'un vol spécifique.

**Response (200 OK):** Un seul objet Flight (voir format ci-dessus)

**Response (404 Not Found):**
```json
{
  "error": "Vol non trouvé",
  "message": "Aucun vol avec l'ID 123"
}
```

### POST /flights

Crée un nouveau vol.

**Request Body:**
```json
{
  "callsign": "AF1234",
  "aircraftType": "A320",
  "company": "Air France",
  "nature": "PAX",
  "origin": "CDG",
  "destination": "LFW",
  "eta": "2024-12-10T16:30:00",
  "etd": "2024-12-10T18:00:00",
  "passengers": 150,
  "cargoWeight": 2000,
  "notes": "Vol prioritaire"
}
```

**Response (201 Created):** Objet Flight créé

### PUT /flights/{id}

Met à jour un vol existant.

**Request Body:** Même format que POST (tous les champs ou partiels)

**Response (200 OK):** Objet Flight mis à jour

### DELETE /flights/{id}

Supprime un vol.

**Response (204 No Content)**

### POST /flights/{flightId}/assign-poste/{posteId}

Attribue un poste à un vol.

**Response (200 OK):** Objet Flight avec `assignedPosteId` et `assignedPosteCode` mis à jour

### GET /flights/search?q={searchTerm}

Recherche des vols par callsign, compagnie, etc.

**Query Parameters:**
- `q` (required): Terme de recherche

**Response (200 OK):** Array d'objets Flight

---

## 3. Postes (Parking Stands)

### GET /postes

Récupère tous les postes.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "code": "ST-01",
    "type": "PAX",
    "status": "LIBRE",
    "zone": "A",
    "maxCode": 4,
    "hasJetBridge": true,
    "hasGroundPower": true,
    "hasPreconditionedAir": true,
    "hasFuelService": true,
    "latitude": 6.1656,
    "longitude": 1.2544,
    "occupiedByFlightId": null,
    "occupiedByCallsign": null,
    "occupiedSince": null,
    "reservedUntil": null,
    "constraints": "Avions de taille moyenne uniquement",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-12-10T14:30:00"
  }
]
```

### GET /postes/available

Récupère uniquement les postes disponibles (status = LIBRE).

**Response:** Même format que `/postes`

### GET /postes/{id}

Récupère les détails d'un poste spécifique.

**Response (200 OK):** Un seul objet Poste

### POST /postes

Crée un nouveau poste (Admin uniquement).

**Request Body:**
```json
{
  "code": "ST-19",
  "type": "PAX",
  "zone": "C",
  "maxCode": 3,
  "hasJetBridge": false,
  "hasGroundPower": true,
  "hasPreconditionedAir": true,
  "hasFuelService": true,
  "latitude": 6.1656,
  "longitude": 1.2544,
  "constraints": ""
}
```

**Response (201 Created):** Objet Poste créé

### PUT /postes/{id}

Met à jour un poste existant (Admin/Ops uniquement).

**Request Body:** Même format que POST

**Response (200 OK):** Objet Poste mis à jour

### DELETE /postes/{id}

Supprime un poste (Admin uniquement).

**Response (204 No Content)**

### POST /postes/{id}/release

Libère un poste occupé.

**Response (200 OK):** Objet Poste avec status = LIBRE

### GET /postes/occupation-rate

Calcule le taux d'occupation global.

**Response (200 OK):**
```json
0.75
```

(Valeur entre 0 et 1, représentant 75% d'occupation)

---

## 4. Alertes

### GET /alerts

Récupère toutes les alertes.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "type": "SATURATION",
    "severity": "HIGH",
    "status": "NEW",
    "title": "Saturation imminente",
    "message": "Le taux d'occupation atteint 95%",
    "details": "16 postes sur 18 sont occupés. Action requise.",
    "relatedFlightId": null,
    "relatedPosteId": null,
    "assignedToUser": "john.doe",
    "acknowledgedAt": null,
    "acknowledgedBy": null,
    "createdAt": "2024-12-10T14:30:00",
    "resolvedAt": null
  }
]
```

### GET /alerts/active

Récupère uniquement les alertes actives (status != RESOLVED).

**Response:** Même format que `/alerts`

### GET /alerts/{id}

Récupère les détails d'une alerte spécifique.

**Response (200 OK):** Un seul objet Alert

### POST /alerts/{id}/acknowledge

Marque une alerte comme reconnue.

**Response (200 OK):** Objet Alert avec `status` = ACKNOWLEDGED et `acknowledgedAt` mis à jour

### POST /alerts/{id}/resolve

Marque une alerte comme résolue.

**Request Body:**
```json
"Problème résolu par réaffectation des vols"
```

**Response (200 OK):** Objet Alert avec `status` = RESOLVED et `resolvedAt` mis à jour

### DELETE /alerts/{id}

Supprime/ignore une alerte.

**Response (204 No Content)**

---

## 5. Énumérations

### FlightNature
- `PAX` - Passagers
- `CARGO` - Fret
- `VIP` - VIP
- `MIL` - Militaire

### FlightStatus
- `SCHEDULED` - Planifié
- `EN_ROUTE` - En Route
- `APPROACHING` - En Approche
- `LANDED` - Atterri
- `PARKED` - Stationné
- `DEPARTED` - Parti
- `CANCELLED` - Annulé
- `DELAYED` - Retardé

### PosteType
- `PAX` - Passagers
- `CARGO` - Fret
- `VIP` - VIP
- `MILITARY` - Militaire
- `GENERAL` - Général

### PosteStatus
- `LIBRE` - Libre (RGB: #4CAF50)
- `OCCUPE` - Occupé (RGB: #F44336)
- `RESERVE` - Réservé (RGB: #FF9800)
- `MAINTENANCE` - Maintenance (RGB: #9E9E9E)
- `MILITARY` - Militaire (RGB: #2196F3)

### PosteZone
- `A` - Zone A (Proche Terminal)
- `B` - Zone B (Moyenne Distance)
- `C` - Zone C (Éloignée)
- `MILITARY` - Parking Militaire

### AlertType
- `SATURATION` - Saturation
- `CONFLICT` - Conflit
- `DELAY` - Retard
- `EMERGENCY` - Urgence
- `WEATHER` - Météo
- `TECHNICAL` - Technique
- `SECURITY` - Sécurité
- `INFO` - Information

### AlertSeverity
- `LOW` - Faible (RGB: #4CAF50)
- `MEDIUM` - Moyenne (RGB: #FF9800)
- `HIGH` - Élevée (RGB: #F44336)
- `CRITICAL` - Critique (RGB: #9C27B0)

### AlertStatus
- `NEW` - Nouvelle
- `ACKNOWLEDGED` - Reconnue
- `IN_PROGRESS` - En Cours
- `RESOLVED` - Résolue
- `IGNORED` - Ignorée

### UserRole
- `ADMIN` - Administrateur (tous droits)
- `OPS` - Opérateur (modification vols/postes)
- `ATC` - Contrôleur ATC (lecture seule postes)
- `VIEWER` - Observateur (lecture seule)

---

## 6. Codes d'Erreur HTTP

- **200 OK** : Requête réussie
- **201 Created** : Ressource créée
- **204 No Content** : Succès sans contenu
- **400 Bad Request** : Requête invalide
- **401 Unauthorized** : Non authentifié
- **403 Forbidden** : Droits insuffisants
- **404 Not Found** : Ressource non trouvée
- **409 Conflict** : Conflit (ex: poste déjà occupé)
- **500 Internal Server Error** : Erreur serveur

---

## 7. Format des Dates

Toutes les dates sont au format **ISO 8601** :

```
YYYY-MM-DDTHH:mm:ss
```

Exemple : `2024-12-10T14:30:00`

Timezone : UTC

---

## 8. Pagination (Optionnel)

Pour les grandes listes, le backend peut implémenter la pagination :

**Query Parameters:**
- `page` : Numéro de page (commence à 0)
- `size` : Taille de page (défaut: 50)
- `sort` : Champ de tri (ex: `createdAt,desc`)

**Response avec pagination:**
```json
{
  "content": [...],
  "page": 0,
  "size": 50,
  "totalElements": 150,
  "totalPages": 3
}
```

---

## 9. WebSocket (Temps Réel) - Optionnel

Pour les mises à jour en temps réel, le backend peut exposer des endpoints WebSocket :

### WS /ws/flights

Envoie des mises à jour de position de vols toutes les 5 secondes.

### WS /ws/postes

Envoie des mises à jour de statut de postes.

### WS /ws/alerts

Envoie de nouvelles alertes en temps réel.

---

## 10. Notes d'Implémentation

1. **Validation** : Le backend doit valider toutes les entrées
2. **Sécurité** : Utiliser HTTPS en production
3. **CORS** : Configurer CORS pour autoriser le frontend
4. **Rate Limiting** : Limiter les requêtes pour éviter les abus
5. **Logs** : Logger toutes les actions importantes
6. **Cache** : Implémenter du cache pour les données peu changeantes

---

## Contact Backend

Pour toute question sur l'API :
- **Email** : backend-team@aige.tg
- **Slack** : #backend-api

---

© AIGE - Direction des Systèmes d'Information
Version 3.0.0 - Décembre 2024
