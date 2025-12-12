# Problème de Login : Bad Credentials

## 🔍 Problème Identifié

L'authentification échoue avec l'erreur "Bad credentials" même si l'utilisateur existe dans la base de données.

### Symptômes

```
=== LOGIN ATTEMPT ===
Email: hhh@example.com
Password provided: hhhh123
User found: hhh@example.com
DB Password hash: $2a$10$53CqEVRyzDsRt4EWx4frceJSsO.Mm2feohqmzLAW1sWdyeeYwqxha
Manual password check: false
Authentication failed: Bad credentials
```

## 🔎 Causes Possibles

### 1. Mot de Passe Incorrect (Le Plus Probable)

Le mot de passe fourni (`hhhh123`) ne correspond pas au hash stocké dans la base de données.

**Solutions :**
- Vérifier le mot de passe utilisé lors de la création du compte
- Recréer l'utilisateur avec un mot de passe connu
- Réinitialiser le mot de passe

### 2. Hash de Mot de Passe Corrompu

Le hash stocké dans la base de données pourrait être incorrect ou mal formaté.

**Vérification :**
- Le hash doit commencer par `$2a$` ou `$2b$` pour BCrypt
- Le hash doit avoir une longueur d'environ 60 caractères

### 3. Problème de Configuration PasswordEncoder

Le `PasswordEncoder` utilisé pour vérifier ne correspond pas à celui utilisé pour encoder.

**Vérification :**
- Les deux doivent être des instances de `BCryptPasswordEncoder`
- Même configuration (strength)

## ✅ Corrections Appliquées

### 1. Vérification Préalable du Mot de Passe

Le code vérifie maintenant le mot de passe **avant** d'appeler `authenticationManager.authenticate()`, ce qui permet de :
- Retourner un message d'erreur plus clair
- Éviter les exceptions inutiles
- Améliorer les logs de diagnostic

### 2. Message d'Erreur Unifié

Tous les cas d'erreur retournent maintenant le même message : `"Invalid email or password"` pour éviter de révéler si l'email existe ou non (sécurité).

## 🧪 Tests à Effectuer

### Test 1 : Vérifier le Mot de Passe

1. **Créer un nouvel utilisateur** avec un mot de passe connu :
```json
POST http://localhost:7020/api/users/register
{
  "email": "test@test.com",
  "password": "test123",
  "role": "PARTICIPANT"
}
```

2. **Se connecter avec ce mot de passe** :
```json
POST http://localhost:7020/api/users/login
{
  "email": "test@test.com",
  "password": "test123"
}
```

### Test 2 : Vérifier le Hash

Si l'utilisateur existe déjà, vérifier le hash dans la base de données :

```sql
SELECT email, password FROM users WHERE email = 'hhh@example.com';
```

Le hash doit être un BCrypt valide (commence par `$2a$` ou `$2b$`).

### Test 3 : Recréer l'Utilisateur

Si le problème persiste, supprimer et recréer l'utilisateur :

1. Supprimer l'utilisateur de la base de données
2. Recréer avec un mot de passe connu
3. Tester la connexion

## 🔧 Solution Rapide

### Option 1 : Recréer l'Utilisateur

```json
POST http://localhost:7020/api/users/register
{
  "email": "hhh@example.com",
  "password": "hhhh123",
  "role": "PARTICIPANT"
}
```

**Note :** Cela échouera si l'email existe déjà. Dans ce cas, supprimer d'abord l'utilisateur de la base de données.

### Option 2 : Utiliser un Nouvel Email

Créer un nouvel utilisateur avec un email différent :

```json
POST http://localhost:7020/api/users/register
{
  "email": "newuser@example.com",
  "password": "password123",
  "role": "PARTICIPANT"
}
```

Puis tester la connexion :

```json
POST http://localhost:7020/api/users/login
{
  "email": "newuser@example.com",
  "password": "password123"
}
```

## 📋 Checklist de Diagnostic

- [ ] L'utilisateur existe dans la base de données
- [ ] Le hash du mot de passe est valide (commence par `$2a$` ou `$2b$`)
- [ ] Le mot de passe fourni correspond à celui utilisé lors de la création
- [ ] Le `PasswordEncoder` est correctement configuré
- [ ] Tester avec un nouvel utilisateur créé avec un mot de passe connu

## 🎯 Résultat Attendu

Après correction, la connexion devrait fonctionner :

```json
POST http://localhost:7020/api/users/login
{
  "email": "test@test.com",
  "password": "test123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "test@test.com"
}
```
