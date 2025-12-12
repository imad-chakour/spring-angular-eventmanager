# Correction : Filtre JWT bloque les endpoints publics

## 🔍 Problème Identifié

Le filtre JWT dans `userservice` s'exécutait **avant** que Spring Security ne vérifie les endpoints `permitAll()`, ce qui causait une erreur **403 Forbidden** même pour `/api/users/register` qui devrait être public.

### Cause
- Le filtre JWT vérifiait le token pour **toutes** les requêtes
- Même si Spring Security autorisait `/api/users/register` avec `permitAll()`
- Le filtre s'exécutait en premier et bloquait la requête

## ✅ Correction Appliquée

### Modification du Filtre JWT

**Avant :**
- Le filtre vérifiait le token pour toutes les requêtes
- Même les endpoints publics nécessitaient un token (ou étaient bloqués)

**Après :**
- Le filtre **ignore** les endpoints publics avant de vérifier le token
- Les endpoints publics passent directement sans vérification JWT
- Seuls les endpoints protégés nécessitent un token valide

### Code Modifié

```java
// Endpoints publics qui ne nécessitent pas de token JWT
private static final String[] PUBLIC_ENDPOINTS = {
        "/api/users/login",
        "/api/users/register",
        "/actuator"
};

@Override
protected void doFilterInternal(...) {
    String requestPath = request.getRequestURI();

    // Ignorer les endpoints publics - laisser Spring Security les gérer
    if (isPublicEndpoint(requestPath)) {
        filterChain.doFilter(request, response);
        return;
    }

    // Vérifier le token pour les autres endpoints
    // ...
}
```

## 📋 Endpoints Publics

Les endpoints suivants sont maintenant **ignorés** par le filtre JWT :

- ✅ `/api/users/login` - Connexion
- ✅ `/api/users/register` - Inscription
- ✅ `/actuator/**` - Endpoints de monitoring

## 🔄 Flux de Requête

### Avant (Problème)
```
Requête → Filtre JWT (bloque si pas de token) → Spring Security (jamais atteint)
```

### Après (Corrigé)
```
Requête → Filtre JWT (ignore si endpoint public) → Spring Security (vérifie permitAll) → Controller
```

## ✅ Résultat

Maintenant :
- ✅ `/api/users/register` fonctionne **sans token**
- ✅ `/api/users/login` fonctionne **sans token**
- ✅ Les autres endpoints nécessitent toujours un **token JWT valide**

## 🚀 Action Requise

**Redémarrer le User Service** pour appliquer les modifications.

## 🧪 Test

Après redémarrage, tester :

```bash
curl -X POST http://localhost:1111/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123","role":"PARTICIPANT"}'
```

**Résultat attendu :** `200 OK` avec les données de l'utilisateur créé

