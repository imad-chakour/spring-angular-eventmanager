# 📊 Comment Fonctionne le Service Analytics

## 🔍 Fonctionnement Actuel

Le service **Analytics** fonctionne comme un **système de stockage et récupération de métriques**. Il ne calcule **PAS automatiquement** les métriques au démarrage.

### Architecture

```
┌─────────────────┐
│  Analytics      │
│   Service       │
└────────┬────────┘
         │
         ├──► CampaignMetrics (table: campaign_metrics)
         │    - Stocke les métriques des campagnes
         │    - emailsSent, emailsOpened, clicks, etc.
         │
         └──► EventMetrics (table: event_metrics)
              - Stocke les métriques des événements
              - totalRegistrations, attendanceRate, etc.
```

## ✅ Nouveautés Ajoutées

### 1. Calcul Automatique des Métriques

J'ai ajouté des méthodes pour **calculer automatiquement** les métriques à partir des campagnes et événements existants :

#### Pour les Campagnes
- Récupère toutes les campagnes via `CampaignClient`
- Crée ou met à jour les métriques pour chaque campagne
- Calcule les taux (openRate, clickRate, conversionRate)

#### Pour les Événements
- Récupère tous les événements via `EventClient`
- Récupère les inscriptions pour chaque événement
- Calcule les métriques (totalRegistrations, attendanceRate, etc.)

### 2. Nouveaux Endpoints

#### Calculer les Métriques des Campagnes
```
POST http://localhost:1111/api/analytics/campaigns/calculate
Authorization: Bearer <token>
```

#### Calculer les Métriques des Événements
```
POST http://localhost:1111/api/analytics/events/calculate
Authorization: Bearer <token>
```

#### Calculer Toutes les Métriques
```
POST http://localhost:1111/api/analytics/calculate-all
Authorization: Bearer <token>
```

## 🚀 Utilisation

### Étape 1 : Calculer les Métriques

**Option A : Calculer toutes les métriques**
```
POST http://localhost:1111/api/analytics/calculate-all
Authorization: Bearer <token>
```

**Option B : Calculer séparément**
```
POST http://localhost:1111/api/analytics/campaigns/calculate
POST http://localhost:1111/api/analytics/events/calculate
```

### Étape 2 : Récupérer les Métriques

```
GET http://localhost:1111/api/analytics/campaigns
GET http://localhost:1111/api/analytics/events
Authorization: Bearer <token>
```

## 📋 Ce qui est Calculé

### CampaignMetrics
- `emailsSent` : Nombre d'emails envoyés (à partir des données de campagne)
- `emailsDelivered` : Emails livrés
- `emailsOpened` : Emails ouverts
- `clicks` : Clics sur les liens
- `conversions` : Conversions
- `openRate` : Taux d'ouverture (emailsOpened / emailsSent * 100)
- `clickRate` : Taux de clic (clicks / emailsSent * 100)
- `conversionRate` : Taux de conversion (conversions / emailsSent * 100)

### EventMetrics
- `totalRegistrations` : Nombre total d'inscriptions (depuis EventService)
- `confirmedRegistrations` : Inscriptions confirmées
- `actualAttendance` : Présence réelle (currentParticipants de l'événement)
- `attendanceRate` : Taux de présence (actualAttendance / maxCapacity * 100)
- `cancellationRate` : Taux d'annulation (cancelled / totalRegistrations * 100)
- `satisfactionScore` : Score de satisfaction (à définir)

## ⚠️ Notes Importantes

1. **Les métriques ne sont PAS calculées automatiquement au démarrage**
   - Vous devez appeler les endpoints `/calculate` pour générer les métriques

2. **Les métriques sont basées sur les données existantes**
   - Si aucune campagne/événement n'existe, aucune métrique ne sera créée

3. **Les métriques sont mises à jour, pas recréées**
   - Si des métriques existent déjà pour une campagne/événement, elles sont mises à jour

4. **Les valeurs sont des simulations pour l'instant**
   - Les métriques de campagnes (emailsSent, etc.) sont initialisées à 0
   - Vous pouvez les mettre à jour manuellement via POST

## 🛠️ Améliorations Futures Possibles

1. **Calcul automatique périodique** (via @Scheduled)
2. **Intégration avec le service de notifications** pour les métriques d'emails
3. **Calcul réel des métriques** à partir des données de tracking
4. **Agrégation des métriques** par période (jour, semaine, mois)

## 📝 Fichiers Modifiés

- `backend/analyticsservice/src/main/java/com/example/analyticsservice/service/AnalyticsService.java`
  - Ajout de `calculateCampaignMetrics()`
  - Ajout de `calculateEventMetrics()`
  - Ajout de méthodes helper pour extraire les valeurs des Maps

- `backend/analyticsservice/src/main/java/com/example/analyticsservice/controller/AnalyticsController.java`
  - Ajout de `POST /campaigns/calculate`
  - Ajout de `POST /events/calculate`
  - Ajout de `POST /calculate-all`

- `backend/analyticsservice/src/main/java/com/example/analyticsservice/client/CampaignClient.java`
  - Ajout de `getAllCampaigns()`

- `backend/analyticsservice/src/main/java/com/example/analyticsservice/client/EventClient.java`
  - Ajout de `getAllEvents()`
  - Ajout de `getRegistrationsByEvent()`
  - Correction du nom du service : `event-service` → `eventservice`

## 🎯 Prochaines Étapes

1. **Redémarrer le AnalyticsService** (port 9010)
2. **Appeler l'endpoint de calcul** pour générer les métriques
3. **Vérifier que les métriques s'affichent** dans le frontend
