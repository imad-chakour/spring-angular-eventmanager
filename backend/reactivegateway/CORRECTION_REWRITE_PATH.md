# Correction : Erreur 403 causée par le Rewrite Path

## 🔍 Problème Identifié

Le gateway transformait `/api/users/register` en `/api/register` via le rewrite path, mais le filtre JWT du `userservice` cherchait `/api/users/register` dans les endpoints publics.

### Flux Avant Correction

```
Client → Gateway: /api/users/register
Gateway → Rewrite: /api/register
Gateway → User Service: /api/register
User Service JWT Filter: Cherche /api/users/register dans PUBLIC_ENDPOINTS
❌ Pas trouvé → 403 Forbidden
```

### Flux Après Correction

```
Client → Gateway: /api/users/register
Gateway → User Service: /api/users/register (pas de rewrite)
User Service JWT Filter: Trouve /api/users/register dans PUBLIC_ENDPOINTS
✅ Endpoint public → Autorise
```

## ✅ Correction Appliquée

**Fichier :** `GatewayRoutesConfig.java`

**Avant :**
```java
.route("user-service-api", r -> r
    .path("/api/users/**")
    .filters(f -> f.rewritePath("/api/users/(?<segment>.*)", "/api/${segment}"))
    .uri("lb://userservice"))
```

**Après :**
```java
.route("user-service-api", r -> r
    .path("/api/users/**")
    .uri("lb://userservice"))  // Pas de rewrite - garde /api/users/**
```

## 📋 Pourquoi Pas de Rewrite ?

Le `userservice` a déjà les bons paths :
- Controller : `@RequestMapping("/api/users")`
- Endpoints : `/api/users/login`, `/api/users/register`
- Filtre JWT : Cherche `/api/users/login` et `/api/users/register`

En gardant le path complet, le filtre JWT reconnaît correctement les endpoints publics.

## 🚀 Action Requise

**Redémarrer le Reactive Gateway** pour appliquer la modification.

## ✅ Résultat Attendu

Après redémarrage :
- ✅ `POST http://localhost:1111/api/users/register` → **200 OK**
- ✅ `POST http://localhost:1111/api/users/login` → **200 OK**
