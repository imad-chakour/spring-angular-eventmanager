# 📊 Utilisation du Service Analytics

## 🔍 Comment ça Fonctionne

Le service **Analytics** stocke et récupère les métriques des campagnes et événements. **Les métriques ne sont PAS calculées automatiquement** - vous devez les générer manuellement.

## ✅ Nouveautés Ajoutées

### Calcul Automatique des Métriques

J'ai ajouté des endpoints pour **calculer automatiquement** les métriques à partir des campagnes et événements existants :

1. **Pour les Campagnes** : Récupère toutes les campagnes et crée/met à jour les métriques
2. **Pour les Événements** : Récupère tous les événements et leurs inscriptions, puis calcule les métriques

## 🚀 Utilisation

### Étape 1 : Calculer les Métriques

**Via Postman :**
```
POST http://localhost:1111/api/analytics/calculate-all
Authorization: Bearer <token>
```

**Via le Frontend :**
- Aller sur la page `/analytics`
- Cliquer sur le bouton **"📊 Calculer les Métriques"**

### Étape 2 : Vérifier les Métriques

```
GET http://localhost:1111/api/analytics/campaigns
GET http://localhost:1111/api/analytics/events
Authorization: Bearer <token>
```

## 📋 Endpoints Disponibles

### Récupération
- `GET /api/analytics/campaigns` - Toutes les métriques de campagnes
- `GET /api/analytics/campaigns/{campaignId}` - Métriques d'une campagne
- `GET /api/analytics/events` - Toutes les métriques d'événements
- `GET /api/analytics/events/{eventId}` - Métriques d'un événement

### Calcul Automatique
- `POST /api/analytics/campaigns/calculate` - Calculer les métriques des campagnes
- `POST /api/analytics/events/calculate` - Calculer les métriques des événements
- `POST /api/analytics/calculate-all` - Calculer toutes les métriques

### Création Manuelle
- `POST /api/analytics/campaigns` - Créer des métriques de campagne manuellement
- `POST /api/analytics/events` - Créer des métriques d'événement manuellement

## 📊 Ce qui est Calculé

### CampaignMetrics
- `totalRegistrations` : Nombre total d'inscriptions (depuis EventService)
- `confirmedRegistrations` : Inscriptions confirmées (status = CONFIRMED)
- `actualAttendance` : Présence réelle (currentParticipants de l'événement)
- `attendanceRate` : Taux de présence (actualAttendance / maxCapacity * 100)
- `cancellationRate` : Taux d'annulation (cancelled / totalRegistrations * 100)

### EventMetrics
- `emailsSent`, `emailsDelivered`, `emailsOpened`, `clicks`, `conversions`
- `openRate`, `clickRate`, `conversionRate` (calculés automatiquement)

## ⚠️ Notes Importantes

1. **Les métriques doivent être calculées manuellement**
   - Appeler `/calculate-all` ou utiliser le bouton dans le frontend

2. **Les métriques sont basées sur les données existantes**
   - Si aucune campagne/événement n'existe, aucune métrique ne sera créée

3. **Les métriques sont mises à jour, pas recréées**
   - Si des métriques existent déjà, elles sont mises à jour avec les nouvelles valeurs

## 🎯 Prochaines Étapes

1. **Redémarrer le AnalyticsService** (port 9010)
2. **S'assurer que des campagnes et événements existent**
3. **Appeler `/calculate-all` ou utiliser le bouton dans le frontend**
4. **Vérifier que les métriques s'affichent**
