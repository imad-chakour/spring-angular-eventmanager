# Correction : Erreur 403 Forbidden sur /api/users/register

## 🔍 Problème Identifié

L'erreur **403 Forbidden** était causée par un **décalage entre les noms de services** dans les routes du gateway et les noms réels dans Eureka.

### Cause
- Le gateway utilisait `lb://USERSERVICE` (majuscules)
- Le service est enregistré dans Eureka comme `userservice` (minuscules)
- Même avec `lower-case-service-id=true`, il faut utiliser le nom exact

## ✅ Corrections Appliquées

### 1. Correction des Noms de Services dans les Routes

**Avant :**
```java
.uri("lb://USERSERVICE")
.uri("lb://CAMPAIGNSERVICE")
.uri("lb://EVENTSERVICE")
// etc.
```

**Après :**
```java
.uri("lb://userservice")
.uri("lb://campaignservice")
.uri("lb://eventservice")
// etc.
```

### 2. Ajout de Logs de Debug

Ajout de logs dans `JwtAuthenticationFilter` pour diagnostiquer :
- Path de la requête
- Méthode HTTP
- Décision du filtre (public/authentifié)

### 3. Filtre JWT Amélioré

- ✅ Laisse passer les requêtes OPTIONS (preflight CORS)
- ✅ Laisse passer les endpoints publics (`/api/users/login`, `/api/users/register`)
- ✅ Logs ajoutés pour le débogage

## 📋 Vérifications Requises

### 1. Vérifier que les Services sont Enregistrés dans Eureka

Ouvrir : `http://localhost:8761`

Vérifier que les services suivants sont visibles :
- ✅ `userservice` (port 7020)
- ✅ `campaignservice` (port 9020)
- ✅ `eventservice` (port 9030)
- ✅ `participantservice` (port 9040)
- ✅ `analyticsservice` (port 9010)
- ✅ `notificationservice` (port 7010)
- ✅ `reactivegateway` (port 1111)

### 2. Redémarrer le Gateway

**IMPORTANT :** Redémarrer le gateway après les modifications pour que les nouvelles routes soient prises en compte.

### 3. Tester l'Inscription

```bash
curl -X POST http://localhost:1111/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123","role":"PARTICIPANT"}'
```

**Résultat attendu :** `200 OK` avec les données de l'utilisateur créé

## 🔧 Ordre de Démarrage Recommandé

1. **Eureka Server** (port 8761)
2. **Config Server** (port 8888) - optionnel
3. **User Service** (port 7020)
4. **Reactive Gateway** (port 1111)
5. **Autres services** (campaigns, events, etc.)

## 📝 Routes Corrigées

Toutes les routes utilisent maintenant les noms de services en minuscules :

| Route Gateway | Service Eureka | Port |
|--------------|----------------|------|
| `/api/users/**` | `userservice` | 7020 |
| `/api/campaigns/**` | `campaignservice` | 9020 |
| `/api/events/**` | `eventservice` | 9030 |
| `/api/participants/**` | `participantservice` | 9040 |
| `/api/analytics/**` | `analyticsservice` | 9010 |
| `/api/notifications/**` | `notificationservice` | 7010 |

## ✅ Résultat

Après redémarrage du gateway, l'inscription devrait fonctionner correctement !

**Test :**
```bash
curl -X POST http://localhost:1111/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123","role":"PARTICIPANT"}'
```

