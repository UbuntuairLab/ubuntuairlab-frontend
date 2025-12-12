# AIGE-APRON-SMART v3.0 - Guide d'Utilisation

## Introduction

AIGE-APRON-SMART est un système intelligent de gestion des postes de stationnement aéroportuaires conçu pour optimiser l'utilisation des ressources et améliorer l'efficacité opérationnelle de l'Aéroport International Gnassingbé Eyadéma.

## Connexion

1. Lancez l'application
2. Entrez votre **identifiant** et **mot de passe**
3. Cliquez sur **Se connecter**

⚠️ **Note** : Vous avez 3 tentatives de connexion. Après 3 échecs, contactez l'administrateur.

## Interface Principale (Dashboard)

### Barre supérieure
- **Logo et titre** : AIGE-APRON-SMART
- **Horloge** : Heure et date en temps réel
- **Informations utilisateur** : Nom et rôle
- **Bouton déconnexion** : Se déconnecter de l'application

### Menu latéral (Sidebar)
6 modules principaux :
1. 📡 **Radar Live** - Visualisation des vols en temps réel
2. 🌐 **Visualisation 3D** - Vue 3D de l'aéroport
3. 🅿️ **Gestion Postes** - Gestion des postes de stationnement
4. 📅 **Planification** - Planning des vols et affectations
5. 🔔 **Alertes** - Notifications et alertes
6. 📊 **Historique** - Consultation des données historiques

## Module 1 : Radar Live

### Fonctionnalités
- **Carte interactive** avec tous les vols dans un rayon de 200 km
- **Liste des vols** avec informations détaillées
- **Filtres** : Par rayon, altitude, compagnie
- **Auto-refresh** : Mise à jour automatique toutes les 5 secondes

### Utilisation
1. **Rechercher un vol** : Utilisez le champ de recherche (callsign, compagnie)
2. **Filtrer les vols** : Sélectionnez un filtre dans les menus déroulants
3. **Ajuster le rayon** : Utilisez le spinner pour modifier le rayon de recherche
4. **Voir les détails** : Cliquez sur un vol dans la liste
5. **Focus sur la carte** : Double-cliquez pour centrer la carte sur le vol

### Codes couleur
- 🔵 **Bleu** : En route
- 🟠 **Orange** : En approche
- 🟢 **Vert** : Atterri/Stationné
- 🔴 **Rouge** : Retardé

## Module 2 : Visualisation 3D

### Modes de visualisation
1. **Mode Normal** : Navigation libre
2. **Mode Prédiction IA** : Trajectoires prédites
3. **Mode Planification** : Curseur temporel
4. **Mode Replay** : Rejeu historique

### Contrôles
- **Rotation** : Clic gauche + glisser
- **Zoom** : Molette de la souris ou slider
- **Panoramique** : Clic droit + glisser
- **Réinitialiser** : Bouton "Réinitialiser"

### Options
- ☁️ **Météo** : Afficher les effets météorologiques
- 🏷️ **Étiquettes** : Afficher les labels des postes

## Module 3 : Gestion des Postes

### Vue d'ensemble
- **18 postes civils** : ST-01 à ST-18
- **4 postes militaires** : PM-01 à PM-04
- **Taux d'occupation** : Barre de progression en haut

### Codes couleur des postes
- 🟢 **Vert (RGB 76,175,80)** : Libre
- 🔴 **Rouge (RGB 244,67,54)** : Occupé
- 🟠 **Orange (RGB 255,152,0)** : Réservé
- 🔵 **Bleu (RGB 33,150,243)** : Militaire (temporaire)
- ⚫ **Gris** : Maintenance

### Actions disponibles
1. **Sélectionner un poste** : Clic simple
2. **Voir les détails** : Double-clic
3. **Modifier** : Bouton "Modifier" (Admin/Ops uniquement)
4. **Libérer** : Bouton "Libérer" (si occupé)
5. **Réserver** : Bouton "Réserver" (si libre)

### Filtres
- **Zone** : A (proche), B (moyenne), C (éloignée), Militaire
- **État** : Libre, Occupé, Réservé, Maintenance
- **Type** : PAX, CARGO, VIP, Militaire, Général

## Module 4 : Planification

### Vues disponibles
- **Jour** : Timeline 06:00-24:00
- **Semaine** : 7 jours
- **Mois** : Vue globale
- **Ressources** : Vue par poste

### Créer un vol
1. Cliquez sur **"➕ Créer Vol"**
2. Remplissez le formulaire :
   - **Callsign** (obligatoire)
   - **Type d'avion** (obligatoire)
   - **ETA/ETD** (obligatoire)
   - **Nature** : PAX, CARGO, VIP, MIL
   - **Compagnie**, **Origine/Destination**
3. Cliquez sur **"Valider"**
4. Le système IA proposera des suggestions

### Simuler un scénario
- Cliquez sur **"🎯 Simuler"**
- Configurez les paramètres
- Visualisez les résultats

### Exporter
- Cliquez sur **"📥 Exporter"**
- Choisissez le format : CSV, PDF, iCal

## Module 5 : Alertes

### Types d'alertes
- 🔴 **Saturation** : Taux d'occupation > 95%
- ⚠️ **Conflit** : Conflit d'affectation
- ⏰ **Retard** : Retard important
- 🚨 **Urgence** : Situation d'urgence
- ☁️ **Météo** : Conditions météo défavorables
- 🔧 **Technique** : Problème technique
- 🔒 **Sécurité** : Alerte sécurité

### Niveaux de gravité
- 🟢 **Faible** : Information
- 🟠 **Moyenne** : Attention requise
- 🔴 **Élevée** : Action urgente
- 🟣 **Critique** : Action immédiate

### Actions
1. **Reconnaître** : Marquer comme lue
2. **Résoudre** : Marquer comme résolue (avec commentaire)
3. **Ignorer** : Archiver l'alerte

### Filtres
- Par **gravité**
- Par **état** : Nouvelle, Reconnue, En Cours, Résolue

## Module 6 : Historique

### Recherche avancée
1. Sélectionnez la **période** (date début/fin)
2. Choisissez le **type** : Vols, Postes, Alertes, Utilisateurs
3. Entrez des **mots-clés**
4. Cliquez sur **"🔍 Rechercher"**

### Export des résultats
- Cliquez sur **"📥 Exporter"**
- Formats disponibles : CSV, Excel, PDF

## Rôles et Permissions

### Administrateur (ADMIN)
✅ Accès complet à tous les modules
✅ Création/modification/suppression
✅ Gestion des utilisateurs
✅ Paramétrage système

### Opérateur (OPS)
✅ Accès à tous les modules
✅ Modification des vols et postes
✅ Gestion des alertes
❌ Pas de gestion des utilisateurs

### Contrôleur ATC (ATC)
✅ Consultation de tous les modules
✅ Création d'alertes
❌ Pas de modification des postes
❌ Pas de modification des vols

### Observateur (VIEWER)
✅ Consultation uniquement
❌ Aucune modification

## Raccourcis Clavier

- **F5** : Actualiser le module actuel
- **Ctrl+R** : Recharger les données
- **Ctrl+F** : Focus sur la recherche
- **Ctrl+Q** : Déconnexion
- **Échap** : Fermer les dialogues

## Codes d'État des Vols

- **SCHEDULED** : Planifié
- **EN_ROUTE** : En route
- **APPROACHING** : En approche
- **LANDED** : Atterri
- **PARKED** : Stationné
- **DEPARTED** : Parti
- **CANCELLED** : Annulé
- **DELAYED** : Retardé

## Bonnes Pratiques

### Pour les Opérateurs
1. ✅ Vérifier le radar toutes les 5 minutes
2. ✅ Traiter les alertes critiques immédiatement
3. ✅ Mettre à jour les statuts de postes régulièrement
4. ✅ Documenter les incidents dans les notes

### Pour les Administrateurs
1. ✅ Réviser les logs quotidiennement
2. ✅ Vérifier les taux d'occupation
3. ✅ Former les nouveaux utilisateurs
4. ✅ Maintenir la base de données à jour

## Dépannage Courant

### "Erreur de connexion au serveur"
➡️ Vérifiez votre connexion réseau
➡️ Contactez le support technique

### "Session expirée"
➡️ Reconnectez-vous
➡️ Vos données locales sont sauvegardées

### "Données non disponibles"
➡️ Actualisez le module (F5)
➡️ Vérifiez l'état du serveur

### "Permission refusée"
➡️ Contactez votre administrateur
➡️ Vérifiez votre rôle utilisateur

## Support Technique

📧 **Email** : support@aige.tg
📞 **Téléphone** : +228 XX XX XX XX
🕐 **Horaires** : 24/7

## Notes Importantes

⚠️ **Ne partagez jamais vos identifiants**
⚠️ **Déconnectez-vous toujours après utilisation**
⚠️ **Signalez immédiatement les comportements anormaux**
⚠️ **Sauvegardez vos rapports importants localement**

---

© AIGE - Direction des Systèmes d'Information
Version 3.0.0 - Décembre 2024
