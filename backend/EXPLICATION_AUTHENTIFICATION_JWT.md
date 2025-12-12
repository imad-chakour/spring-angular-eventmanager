# 🔐 Explication Complète : Authentification JWT

## 📋 Vue d'Ensemble

Oui, **le JWT token est utilisé** dans votre système ! Voici comment fonctionne l'authentification complète :

## 🔄 Flux d'Authentification Complet

### Étape 1 : Inscription (Register) - Pas de JWT

```
Client → POST /api/users/register
         ↓
Gateway → Vérifie : endpoint public → ✅ Autorise
         ↓
UserService → Crée l'utilisateur avec mot de passe encodé
         ↓
Réponse : Utilisateur créé (sans mot de passe)
```

**Pas de JWT ici** - c'est juste la création du compte.

---

### Étape 2 : Connexion (Login) - Génération du JWT

```
Client → POST /api/users/login
         Body: { "email": "...", "password": "..." }
         ↓
Gateway → Vérifie : endpoint public → ✅ Autorise
         ↓
UserService AuthController.login() :
  1. Vérifie l'email et le mot de passe
  2. Authentifie avec AuthenticationManager
  3. Génère un JWT token avec JwtTokenProvider
         ↓
Réponse : { "token": "eyJhbGciOiJIUzI1NiJ9...", "email": "..." }
```

**C'est ici que le JWT est créé !** 🎯

#### Code dans AuthController.login() :

```java
// 1. Vérification du mot de passe
boolean passwordMatches = passwordEncoder.matches(password, dbUser.getPassword());

// 2. Authentification Spring Security
Authentication authentication = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
);

// 3. Génération du JWT token
String token = jwtTokenProvider.generateToken(authentication);
```

#### Contenu du Token JWT :

Le token contient :
- **Subject** : email de l'utilisateur
- **Roles** : rôles de l'utilisateur (ex: "ROLE_ADMIN,ROLE_FACTOR_PASSWORD")
- **Issued At** : date de création
- **Expiration** : date d'expiration (24h par défaut)

---

### Étape 3 : Accès aux Endpoints Protégés - Utilisation du JWT

```
Client → GET /api/users
         Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         ↓
Gateway JwtAuthenticationFilter :
  1. Vérifie si endpoint public ? → Non
  2. Extrait le token du header Authorization
  3. Valide le token avec JwtTokenValidator
  4. Si valide → Ajoute X-User-Email et X-User-Roles aux headers
  5. Transmet la requête au microservice
         ↓
UserService JwtAuthenticationFilter :
  1. Vérifie si endpoint public ? → Non
  2. Extrait le token du header Authorization
  3. Valide le token avec JwtTokenProvider
  4. Charge les UserDetails depuis la base
  5. Crée l'Authentication et l'ajoute au SecurityContext
         ↓
UserController → Traite la requête avec l'utilisateur authentifié
         ↓
Réponse : Liste des utilisateurs
```

## 🛡️ Double Protection JWT

Votre système a **deux niveaux de validation JWT** :

### 1. Gateway (Reactive Gateway)

**Fichier :** `reactivegateway/filters/JwtAuthenticationFilter.java`

**Rôle :**
- ✅ Valide le token **avant** d'atteindre les microservices
- ✅ Bloque les requêtes sans token valide
- ✅ Ajoute les infos utilisateur (email, roles) aux headers pour les microservices

**Code clé :**
```java
// Valide le token
if (token == null || !jwtTokenValidator.validateToken(token)) {
    // Retourne 401 Unauthorized
}

// Ajoute les infos utilisateur aux headers
ServerHttpRequest modifiedRequest = request.mutate()
    .header("X-User-Email", username)
    .header("X-User-Roles", roles)
    .build();
```

### 2. User Service

**Fichier :** `userservice/security/JwtAuthenticationFilter.java`

**Rôle :**
- ✅ Valide le token **dans** le microservice
- ✅ Charge les UserDetails depuis la base de données
- ✅ Crée l'Authentication Spring Security
- ✅ Ajoute l'utilisateur au SecurityContext

**Code clé :**
```java
// Valide et extrait les infos
if (token != null && tokenProvider.validateToken(token)) {
    String username = tokenProvider.getUsername(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    
    // Crée l'authentification Spring Security
    UsernamePasswordAuthenticationToken auth = 
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    
    // Ajoute au contexte de sécurité
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

## 📦 Composants JWT

### 1. JwtTokenProvider (User Service)

**Fichier :** `userservice/security/JwtTokenProvider.java`

**Responsabilités :**
- ✅ **Génère** les tokens JWT lors du login
- ✅ **Valide** les tokens (vérifie la signature et l'expiration)
- ✅ **Extrait** les informations (username, roles) du token

**Méthodes principales :**
```java
generateToken(Authentication)  // Crée un nouveau token
validateToken(String token)      // Vérifie si le token est valide
getUsername(String token)        // Extrait l'email
getRoles(String token)           // Extrait les rôles
```

### 2. JwtTokenValidator (Gateway)

**Fichier :** `reactivegateway/security/JwtTokenValidator.java`

**Responsabilités :**
- ✅ **Valide** les tokens (même clé secrète que UserService)
- ✅ **Extrait** les informations (username, roles) du token

**Note :** Utilise la **même clé secrète** que `JwtTokenProvider` pour valider les tokens.

## 🔑 Clé Secrète JWT

**Important :** Le Gateway et le UserService doivent utiliser la **même clé secrète** :

**UserService :** `application.properties`
```properties
security.jwt.secret=A4kVIT9eXHnjs8dRXXg3om0HXbQ/MAAev34MH89QM5o=
```

**Gateway :** `application.properties`
```properties
security.jwt.secret=A4kVIT9eXHnjs8dRXXg3om0HXbQ/MAAev34MH89QM5o=
```

Si les clés sont différentes, la validation échouera !

## 📊 Schéma du Flux Complet

```
┌─────────┐
│ Client  │
└────┬────┘
     │
     │ 1. POST /api/users/login
     │    { email, password }
     ▼
┌─────────────────┐
│ Gateway         │
│ (Public)        │ ✅ Autorise
└────┬────────────┘
     │
     │ 2. Transmet au UserService
     ▼
┌─────────────────┐
│ UserService     │
│ AuthController  │
│                 │ ✅ Vérifie email/password
│                 │ ✅ Génère JWT token
└────┬────────────┘
     │
     │ 3. Retourne { token, email }
     ▼
┌─────────┐
│ Client  │ ← Token JWT stocké
└────┬────┘
     │
     │ 4. GET /api/users
     │    Header: Authorization: Bearer <token>
     ▼
┌─────────────────┐
│ Gateway         │
│ JWT Filter      │ ✅ Valide le token
│                 │ ✅ Ajoute X-User-Email, X-User-Roles
└────┬────────────┘
     │
     │ 5. Transmet avec headers
     ▼
┌─────────────────┐
│ UserService     │
│ JWT Filter      │ ✅ Valide le token
│                 │ ✅ Charge UserDetails
│                 │ ✅ Crée Authentication
│ UserController  │ ✅ Traite la requête
└─────────────────┘
```

## 🎯 Points Clés à Retenir

1. **Le JWT est généré lors du login** dans `AuthController.login()`
2. **Le JWT est validé deux fois** :
   - Au niveau du Gateway (première ligne de défense)
   - Au niveau du UserService (validation complète)
3. **Le token contient** : email, roles, dates d'émission/expiration
4. **Le token est envoyé** dans le header `Authorization: Bearer <token>`
5. **Les endpoints publics** (`/login`, `/register`) ne nécessitent pas de token

## 🧪 Test du JWT

### 1. Obtenir un Token

```bash
POST http://localhost:1111/api/users/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZXMiOiJST0xFX1BBUlRJQ0lQQU5UIiwiaWF0IjoxNzM0MTIzNDU2LCJleHAiOjE3MzQyMDk4NTZ9...",
  "email": "test@example.com"
}
```

### 2. Utiliser le Token

```bash
GET http://localhost:1111/api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Résultat :** ✅ Liste des utilisateurs (si token valide)

### 3. Sans Token (Doit Échouer)

```bash
GET http://localhost:1111/api/users
```

**Résultat :** ❌ 401 Unauthorized

## ✅ Résumé

**Oui, le JWT est utilisé !** 

- ✅ Généré lors du **login**
- ✅ Validé au niveau du **Gateway**
- ✅ Validé au niveau du **UserService**
- ✅ Utilisé pour accéder aux **endpoints protégés**
- ✅ Contient les **informations utilisateur** (email, roles)

L'authentification fonctionne en **deux étapes** :
1. **Login** → Obtention du token
2. **Requêtes protégées** → Utilisation du token dans le header Authorization
