# 🔐 Sécurité : Attribution des Rôles Utilisateurs

## 🎯 Problème de Sécurité Identifié

**Avant :** N'importe qui pouvait s'inscrire avec le rôle `ADMIN` ou `MARKETING_MANAGER` via le formulaire d'inscription, ce qui représente un **risque de sécurité majeur**.

## ✅ Corrections Appliquées

### 1. Frontend - Formulaire d'Inscription

**Modifications :**
- ✅ **Retiré** le champ "Rôle" du formulaire d'inscription
- ✅ Les utilisateurs ne peuvent plus choisir leur rôle lors de l'inscription
- ✅ Le formulaire ne contient plus que : email, password, firstName, lastName

**Fichiers modifiés :**
- `frontend/src/app/features/users/components/register/register.component.ts`
- `frontend/src/app/features/users/components/register/register.component.html`

### 2. Backend - Forcer le Rôle PARTICIPANT

**Modifications :**
- ✅ Le backend **force** le rôle `PARTICIPANT` pour toutes les inscriptions
- ✅ Même si un rôle est fourni dans la requête, il est **ignoré**
- ✅ Seul un admin peut modifier les rôles via le formulaire de gestion des utilisateurs

**Fichier modifié :**
- `backend/userservice/src/main/java/com/example/userservice/controller/AuthController.java`

**Code :**
```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {
    // SECURITY: Force PARTICIPANT role for all registrations
    // Role assignment can only be done by admins via user management
    user.setRole(com.example.userservice.model.UserRole.PARTICIPANT);
    
    // ... reste du code
}
```

## 🔒 Protection des Routes d'Administration

### Routes Protégées par AdminGuard

Les routes suivantes sont **protégées** et nécessitent le rôle `ADMIN` :

- ✅ `/users` - Liste des utilisateurs
- ✅ `/users/new` - Créer un utilisateur (admin peut assigner un rôle)
- ✅ `/users/:id/edit` - Modifier un utilisateur (admin peut changer le rôle)

**Configuration :** `frontend/src/app/app.routes.ts`
```typescript
{ 
  path: 'users', 
  component: UserListComponent,
  canActivate: [adminGuard]  // ✅ Protection admin
}
```

## 📋 Flux d'Attribution des Rôles

### 1. Inscription (Public)

```
Utilisateur → POST /api/users/register
             { email, password, firstName, lastName }
             ↓
Backend → Force role = PARTICIPANT
         ↓
Utilisateur créé avec rôle PARTICIPANT
```

### 2. Modification par Admin (Protégé)

```
Admin → GET /users/:id/edit (protégé par adminGuard)
       ↓
Formulaire → Admin peut modifier le rôle
       ↓
Admin → PUT /api/users/:id
       { email, role: ADMIN, ... }
       ↓
Backend → Met à jour le rôle
```

## 🚀 Création d'un Admin Initial

Pour créer le premier administrateur, vous devez :

### Option 1 : Via la Base de Données (Recommandé)

1. Créer un utilisateur normal via l'inscription
2. Se connecter à la base de données
3. Modifier manuellement le rôle en `ADMIN` :

```sql
UPDATE users 
SET role = 'ADMIN' 
WHERE email = 'admin@example.com';
```

### Option 2 : Via un Script SQL Initial

Créer un script SQL pour initialiser un admin :

```sql
INSERT INTO users (email, password, role, status, created_at, updated_at)
VALUES (
  'admin@example.com',
  '$2a$10$...', -- Hash BCrypt du mot de passe
  'ADMIN',
  'ACTIVE',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);
```

**Note :** Générer le hash BCrypt avec un outil ou via le code Java.

### Option 3 : Via l'API (Après avoir un premier admin)

Une fois qu'un admin existe, il peut créer d'autres admins via l'interface de gestion des utilisateurs.

## ✅ Vérifications de Sécurité

### Tests à Effectuer

1. **Test d'Inscription :**
   ```bash
   POST /api/users/register
   {
     "email": "test@example.com",
     "password": "password123",
     "role": "ADMIN"  # Devrait être ignoré
   }
   ```
   **Résultat attendu :** Utilisateur créé avec `role: PARTICIPANT`

2. **Test de Modification par Non-Admin :**
   - Un utilisateur PARTICIPANT ne peut pas accéder à `/users`
   - Redirection vers `/campaigns` si tentative d'accès

3. **Test de Modification par Admin :**
   - Un admin peut accéder à `/users`
   - Un admin peut modifier les rôles des utilisateurs

## 📝 Rôles Disponibles

- `PARTICIPANT` - Rôle par défaut pour tous les nouveaux utilisateurs
- `MARKETING_USER` - Utilisateur marketing (assigné par admin)
- `MARKETING_MANAGER` - Manager marketing (assigné par admin)
- `ADMIN` - Administrateur (assigné manuellement ou par un autre admin)

## 🔐 Bonnes Pratiques

1. ✅ **Toujours** forcer PARTICIPANT lors de l'inscription
2. ✅ **Protéger** les routes de gestion des utilisateurs avec `adminGuard`
3. ✅ **Vérifier** les permissions côté backend avant de modifier les rôles
4. ✅ **Créer** le premier admin manuellement dans la base de données
5. ✅ **Documenter** qui a accès aux comptes admin

## 🚨 Important

- ⚠️ **Ne jamais** permettre l'auto-attribution de rôles privilégiés
- ⚠️ **Toujours** vérifier les permissions côté backend
- ⚠️ **Limiter** le nombre de comptes admin
- ⚠️ **Auditer** régulièrement les changements de rôles
