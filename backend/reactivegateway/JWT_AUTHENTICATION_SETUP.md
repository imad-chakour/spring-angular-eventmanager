# Configuration JWT dans le Reactive Gateway

## ✅ Implémentation Complète

### 1. **Dépendances Ajoutées**
- `jjwt-api` (0.12.3)
- `jjwt-impl` (0.12.3)
- `jjwt-jackson` (0.12.3)

### 2. **Composants Créés**

#### `JwtTokenValidator`
- Valide les tokens JWT
- Extrait le nom d'utilisateur et les rôles
- Utilise la même clé secrète que `userservice`

#### `JwtAuthenticationFilter` (GlobalFilter)
- Filtre global pour valider les tokens JWT
- Exclut les endpoints publics :
  - `/api/users/login`
  - `/api/users/register`
  - `/actuator/**`
- Ajoute les headers `X-User-Email` et `X-User-Roles` aux requêtes forwardées

### 3. **Configuration**

#### `application.properties`
```properties
security.jwt.secret=ZmFrZV9zZWNyZXRfZm9yX2Rldl9vbmx5
security.jwt.expiration-ms=86400000
```

**⚠️ IMPORTANT :** La clé secrète doit être identique à celle du `userservice` !

### 4. **Fonctionnement**

1. **Requête avec JWT valide** :
   - Le filtre valide le token
   - Ajoute `X-User-Email` et `X-User-Roles` aux headers
   - Forward la requête au microservice

2. **Requête sans JWT ou JWT invalide** :
   - Retourne `401 Unauthorized`
   - Message : `{"error":"Unauthorized","message":"Invalid or missing JWT token"}`

3. **Endpoints publics** :
   - `/api/users/login` - Pas de validation JWT
   - `/api/users/register` - Pas de validation JWT
   - `/actuator/**` - Pas de validation JWT

### 5. **Ordre des Filtres**

1. `JwtAuthenticationFilter` (Order: -100) - Validation JWT
2. `MyGlobalLogFilter` (Order: HIGHEST_PRECEDENCE + 1) - Logging
3. Autres filtres

---

## 🔐 Architecture de Sécurité

```
Frontend
    ↓ (JWT dans Authorization header)
Spring Cloud Gateway (Reactive)
    ↓ (Valide JWT)
    ├── Si valide → Ajoute X-User-Email, X-User-Roles
    └── Si invalide → 401 Unauthorized
    ↓
Microservices
    ├── userservice (a sa propre validation JWT)
    ├── campaignservice (fait confiance au gateway)
    ├── eventservice (fait confiance au gateway)
    ├── participantservice (fait confiance au gateway)
    ├── analyticsservice (fait confiance au gateway)
    └── notificationservice (fait confiance au gateway)
```

---

## 📝 Notes Importantes

### Sécurité des Microservices

Les microservices (campaignservice, eventservice, etc.) **ne valident pas le JWT** car :
- Le gateway valide déjà le JWT
- Les services font confiance au gateway
- Cela évite la duplication de code

**Pour une sécurité renforcée**, vous pouvez :
1. Valider le JWT dans chaque service (plus de code à maintenir)
2. Utiliser un service d'authentification centralisé
3. Utiliser OAuth2 avec un Authorization Server

### Headers Ajoutés par le Gateway

Le gateway ajoute automatiquement :
- `X-User-Email` : Email de l'utilisateur authentifié
- `X-User-Roles` : Rôles de l'utilisateur (séparés par virgule)

Les microservices peuvent utiliser ces headers pour :
- Logger les actions utilisateur
- Filtrer les données selon les rôles
- Auditer les accès

---

## 🧪 Test

### Test avec JWT valide
```bash
curl -X GET http://localhost:1111/api/campaigns \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Test sans JWT
```bash
curl -X GET http://localhost:1111/api/campaigns
# Retourne 401 Unauthorized
```

### Test endpoint public
```bash
curl -X POST http://localhost:1111/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'
# Fonctionne sans JWT
```

---

## ✅ Checklist

- ✅ Dépendances JWT ajoutées
- ✅ `JwtTokenValidator` créé
- ✅ `JwtAuthenticationFilter` créé
- ✅ Configuration dans `application.properties`
- ✅ Endpoints publics configurés
- ✅ Headers utilisateur ajoutés
- ✅ Ordre des filtres configuré
- ✅ Documentation créée

---

## 🔧 Configuration de la Clé Secrète

**⚠️ EN PRODUCTION**, changez la clé secrète :

1. Générer une clé secrète sécurisée :
```bash
openssl rand -base64 64
```

2. Mettre à jour dans :
   - `reactivegateway/src/main/resources/application.properties`
   - `userservice/src/main/resources/application.properties`
   - Config Server (si utilisé)

3. Les deux services doivent utiliser **la même clé** !

---

## 🚀 Prêt pour Production

Le gateway est maintenant configuré avec l'authentification JWT ! ✅
