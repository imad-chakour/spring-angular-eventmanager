# 📝 Flux d'Inscription aux Événements

## 🔄 Flux Complet

### 1. Frontend - Liste des Événements

**Composant :** `event-list.component.html`

L'utilisateur voit la liste des événements avec un bouton **"✓ S'inscrire"** pour chaque événement disponible.

**Conditions d'affichage du bouton :**
- ✅ L'événement n'est pas complet (currentParticipants < maxCapacity)
- ✅ L'événement n'est pas terminé/annulé (status != TERMINE, ANNULE, CLOTURE)
- ✅ L'utilisateur est connecté

### 2. Clic sur "S'inscrire"

**Méthode :** `registerForEvent(event: Event)`

**Actions :**
1. Vérifie que l'utilisateur est connecté (via `AuthService.currentUser()`)
2. Affiche une confirmation
3. Appelle `EventService.registerUser(eventId, userId)`

### 3. Frontend - Service EventService

**Méthode :** `registerUser(eventId: number, userId: number)`

**Requête HTTP :**
```
POST http://localhost:1111/api/events/registrations/event/{eventId}/user/{userId}
Authorization: Bearer <token>
Content-Type: application/json
Body: {}
```

### 4. Gateway - Routage

**Route :** `/api/events/**` → `lb://eventservice`

Le gateway route la requête vers le `eventservice` (port 9030).

### 5. Backend - EventController

**Endpoint :** `POST /api/events/registrations/event/{eventId}/user/{userId}`

**Méthode :** `registerUser(eventId, userId)`

### 6. Backend - EventService

**Méthode :** `registerUser(Long eventId, Long userId)`

**Validations :**
1. ✅ Vérifie si l'utilisateur est déjà inscrit
2. ✅ Vérifie que l'événement existe
3. ✅ Vérifie la capacité (si maxCapacity est défini)
4. ✅ Valide l'utilisateur via UserService (Feign)

**Actions :**
1. Crée une nouvelle `Registration` avec status `PENDING`
2. Sauvegarde la registration
3. **Met à jour le nombre de participants** de l'événement (`currentParticipants++`)
4. Retourne la registration créée

### 7. Réponse au Frontend

Le frontend reçoit la registration et :
- Affiche un message de succès
- Recharge la liste des événements pour mettre à jour le nombre de participants

## 📋 Endpoints Disponibles

### Inscription
```
POST /api/events/registrations/event/{eventId}/user/{userId}
```

### Récupération des Inscriptions
```
GET /api/events/registrations
GET /api/events/registrations/event/{eventId}
GET /api/events/registrations/user/{userId}
```

### Suppression d'Inscription
```
DELETE /api/events/registrations/{id}
```

## ✅ Améliorations Appliquées

### Backend
- ✅ Vérification de la capacité avant inscription
- ✅ Mise à jour automatique du nombre de participants
- ✅ Logs détaillés pour le debugging
- ✅ Messages d'erreur explicites

### Frontend
- ✅ Bouton "S'inscrire" dans la liste des événements
- ✅ Vérification que l'utilisateur est connecté
- ✅ Vérification de la disponibilité (capacité, statut)
- ✅ Messages d'erreur/succès
- ✅ Rechargement automatique après inscription

## 🎯 Utilisation

### Pour un Utilisateur

1. **Se connecter** via `/login`
2. **Aller sur la page `/events`**
3. **Voir la liste des événements**
4. **Cliquer sur "✓ S'inscrire"** pour un événement disponible
5. **Confirmer l'inscription**
6. **Voir le message de succès**
7. **Le nombre de participants est mis à jour automatiquement**

### Pour un Admin

Les admins peuvent aussi s'inscrire aux événements de la même manière.

## 📝 Fichiers Modifiés

### Backend
- `backend/eventservice/src/main/java/com/example/event_service/service/EventService.java`
  - Amélioration de `registerUser()` avec vérification de capacité
  - Mise à jour automatique du nombre de participants
  - Logs détaillés

### Frontend
- `frontend/src/app/features/events/event-list.component.ts`
  - Ajout de `registerForEvent()`
  - Ajout de `canRegister()` pour vérifier la disponibilité
  - Injection de `AuthService`

- `frontend/src/app/features/events/event-list.component.html`
  - Ajout du bouton "S'inscrire"
  - Affichage conditionnel selon la disponibilité

## 🚀 Action Requise

**Redémarrer le EventService** (port 9030) pour appliquer les changements.

## ✅ Vérification

1. **Se connecter** avec un compte utilisateur
2. **Aller sur `/events`**
3. **Voir le bouton "S'inscrire"** pour les événements disponibles
4. **Cliquer sur "S'inscrire"**
5. **Vérifier que l'inscription est créée**
6. **Vérifier que le nombre de participants est mis à jour**

