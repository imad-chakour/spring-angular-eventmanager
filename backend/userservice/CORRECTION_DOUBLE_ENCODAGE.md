# Correction : Double Encodage du Mot de Passe

## 🔍 Problème Identifié

Le mot de passe était **encodé deux fois** lors de l'enregistrement :
1. Dans `AuthController.register()` : `passwordEncoder.encode(user.getPassword())`
2. Dans `UserService.saveUser()` : `passwordEncoder.encode(user.getPassword())`

### Conséquence

- Mot de passe stocké : `$2a$10$...` (doublement encodé)
- Mot de passe fourni au login : `password123` (en clair)
- Vérification : `passwordEncoder.matches("password123", "$2a$10$...")` → **false** ❌

## ✅ Corrections Appliquées

### 1. Retrait de l'Encodage dans AuthController

**Avant :**
```java
// Encode password before saving
user.setPassword(passwordEncoder.encode(user.getPassword()));
```

**Après :**
```java
// Password encoding is handled by UserService.saveUser()
// Do NOT encode here to avoid double encoding
```

### 2. Amélioration de UserService.saveUser()

Ajout d'une vérification pour éviter de ré-encoder un mot de passe déjà encodé :

```java
private boolean isPasswordEncoded(String password) {
    return password != null && (password.startsWith("$2a$") || 
                                password.startsWith("$2b$") || 
                                password.startsWith("$2y$"));
}
```

## 🚀 Action Requise

### Pour les Nouveaux Utilisateurs

Les nouveaux utilisateurs fonctionneront correctement après cette correction.

### Pour les Utilisateurs Existants

Les utilisateurs déjà enregistrés ont un mot de passe **doublement encodé**. Il faut :

**Option 1 : Supprimer et Ré-enregistrer (Recommandé)**
1. Supprimer l'utilisateur existant de la base de données
2. Ré-enregistrer avec le même email et mot de passe

**Option 2 : Créer un Nouvel Utilisateur**
- Utiliser un email différent pour tester

## 🧪 Test

### Test 1 : Nouvel Enregistrement
```
POST http://localhost:1111/api/users/register
{
  "email": "newuser@example.com",
  "password": "password123",
  "role": "PARTICIPANT"
}
```

### Test 2 : Login avec Nouvel Utilisateur
```
POST http://localhost:1111/api/users/login
{
  "email": "newuser@example.com",
  "password": "password123"
}
```

**Résultat attendu :** `200 OK` avec token JWT

## 📋 Vérification

Pour vérifier qu'un mot de passe est correctement encodé :
- Format BCrypt : `$2a$10$...` (60 caractères)
- Ne doit PAS être encodé deux fois

## ✅ Résultat

Après correction :
- ✅ Nouveaux utilisateurs : mot de passe encodé **une seule fois**
- ✅ Login fonctionne correctement
- ✅ Vérification du mot de passe réussit
