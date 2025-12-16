# 🔍 Diagnostic : Erreur "0 undefined" - Campagnes

## ❌ Erreur Observée
```
Erreur: Erreur lors du chargement: Http failure response for http://localhost:1111/api/campaigns: 0 undefined
```

## 🔍 Signification

L'erreur **`0 undefined`** indique généralement :
1. **Erreur de connexion réseau** - Le service n'est pas accessible
2. **Problème CORS** - Blocage par le navigateur
3. **Service non démarré** - Le `campaignservice` n'est pas démarré ou non enregistré dans Eureka

## ✅ Corrections Appliquées

### 1. Logs Améliorés
- ✅ Ajout de logs détaillés dans `auth.interceptor.ts`
- ✅ Ajout de logs détaillés dans `campaign.service.ts`
- ✅ Affichage des détails d'erreur dans la console

### 2. Gestion d'Erreur Améliorée
- ✅ Détection des erreurs réseau (status 0)
- ✅ Messages d'erreur plus explicites

## 🧪 Étapes de Diagnostic

### 1. Vérifier la Console du Navigateur (F12)

Ouvrir la console et vérifier les logs :
```javascript
// Vous devriez voir :
AuthInterceptor: Request to http://localhost:1111/api/campaigns
AuthInterceptor: Token present? true/false
CampaignService: Fetching campaigns from: http://localhost:1111/api/campaigns
```

### 2. Vérifier l'Onglet Network (F12 > Network)

1. Recharger la page `/campaigns`
2. Chercher la requête vers `/api/campaigns`
3. Vérifier :
   - **Status** : 200, 401, 403, 500, ou (failed) ?
   - **Headers** : Le header `Authorization: Bearer ...` est-il présent ?
   - **Response** : Quel est le contenu de la réponse ?

### 3. Vérifier que les Services sont Démarrés

#### Vérifier Eureka Dashboard
```
http://localhost:8761
```

**Services attendus :**
- ✅ `userservice` - Status: UP
- ✅ `campaignservice` - Status: UP ← **Vérifier celui-ci**
- ✅ `eventservice` - Status: UP
- ✅ `reactivegateway` - Status: UP

#### Vérifier les Logs du Gateway
Chercher dans les logs du `reactivegateway` :
```
=== Gateway Request ===
Method: GET
Path: /api/campaigns
```

Si vous voyez :
```
No servers available for service: campaignservice
```
→ Le `campaignservice` n'est pas enregistré dans Eureka

### 4. Vérifier le Token JWT

Dans la console du navigateur :
```javascript
localStorage.getItem('auth_token')
```

**Résultat attendu :** Un token JWT (chaîne commençant par `eyJ...`)

**Si null :**
- Se reconnecter via `/login`
- Vérifier que le token est bien sauvegardé après login

### 5. Tester Directement le Backend

#### Via le Gateway
```bash
# PowerShell
$token = "VOTRE_TOKEN_JWT"
Invoke-WebRequest -Uri "http://localhost:1111/api/campaigns" `
  -Method GET `
  -Headers @{"Authorization"="Bearer $token"}
```

#### Directement le Service
```bash
Invoke-WebRequest -Uri "http://localhost:9020/api/campaigns" `
  -Method GET `
  -Headers @{"Authorization"="Bearer $token"}
```

## 🛠️ Solutions selon le Problème

### Problème 1 : Service Non Démarré
**Symptôme :** `No servers available for service: campaignservice`

**Solution :**
1. Démarrer le `campaignservice`
2. Vérifier qu'il s'enregistre dans Eureka
3. Vérifier les logs pour des erreurs de démarrage

### Problème 2 : Token JWT Manquant ou Invalide
**Symptôme :** `AuthInterceptor: Token present? false` ou erreur 401

**Solution :**
1. Se reconnecter via `/login`
2. Vérifier que le token est sauvegardé dans `localStorage`
3. Vérifier que le token n'est pas expiré

### Problème 3 : Erreur CORS
**Symptôme :** Erreur dans la console : `CORS policy: No 'Access-Control-Allow-Origin'`

**Solution :**
- La configuration CORS est déjà en place dans le gateway
- Vérifier que le gateway est bien démarré
- Vérifier que `CorsConfig.java` est bien chargé

### Problème 4 : Erreur de Connexion Réseau
**Symptôme :** Status 0, erreur de connexion

**Solution :**
1. Vérifier que le gateway est démarré (port 1111)
2. Vérifier que le `campaignservice` est démarré (port 9020)
3. Vérifier qu'il n'y a pas de firewall bloquant
4. Vérifier les logs du gateway pour des erreurs de routage

## 📝 Fichiers Modifiés

- `frontend/src/app/core/auth.interceptor.ts`
  - Ajout de logs détaillés
  - Amélioration de la gestion d'erreur

- `frontend/src/app/core/services/campaign.service.ts`
  - Ajout de logs détaillés
  - Import de `catchError` et `throwError`
  - Détection des erreurs réseau

## 🎯 Prochaines Étapes

1. **Ouvrir la console du navigateur (F12)**
2. **Recharger la page `/campaigns`**
3. **Copier tous les logs de la console**
4. **Vérifier l'onglet Network pour la requête `/api/campaigns`**
5. **Vérifier Eureka Dashboard pour voir si `campaignservice` est UP**
6. **Partager les informations pour diagnostic final**

## 💡 Informations à Fournir

Pour un diagnostic complet, fournir :
1. **Logs de la console du navigateur** (F12 > Console)
2. **Détails de la requête HTTP** (F12 > Network > `/api/campaigns`)
3. **Status dans Eureka Dashboard** (`http://localhost:8761`)
4. **Logs du gateway** (chercher "campaignservice")
5. **Logs du campaignservice** (erreurs de démarrage)
