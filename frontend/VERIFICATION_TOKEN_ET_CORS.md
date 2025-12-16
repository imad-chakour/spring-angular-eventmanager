# 🔍 Vérification : Token JWT et CORS

## ❌ Erreur Actuelle
```
Erreur: Erreur lors du chargement: Http failure response for http://localhost:1111/api/campaigns: 0 undefined
```

## 🔍 Diagnostic : Erreur "0 undefined"

L'erreur **`0 undefined`** signifie que la requête **n'a même pas atteint le serveur**. Cela indique généralement :

1. **Problème CORS** - Le navigateur bloque la requête avant qu'elle n'atteigne le serveur
2. **Token manquant** - La requête est rejetée avant d'être envoyée
3. **Service non accessible** - Le service n'est pas démarré ou non accessible
4. **Problème avec `withFetch()`** - L'API fetch peut avoir des problèmes avec CORS

## ✅ Corrections Appliquées

### 1. Logs Détaillés dans l'Interceptor
- ✅ Affichage de l'URL de la requête
- ✅ Vérification de la présence du token
- ✅ Affichage des headers de la requête
- ✅ Détection spécifique de l'erreur status 0

### 2. Logs Détaillés dans le Service
- ✅ Affichage de l'URL appelée
- ✅ Détection des erreurs réseau
- ✅ Messages d'erreur explicites

## 🧪 Tests à Effectuer

### 1. Vérifier le Token dans la Console (F12)

Ouvrir la console du navigateur et exécuter :
```javascript
// Vérifier si le token existe
localStorage.getItem('auth_token')

// Si null, se connecter d'abord
// Puis vérifier à nouveau
```

**Résultat attendu :** Un token JWT (chaîne commençant par `eyJ...`)

### 2. Vérifier les Logs de l'Interceptor

Dans la console, vous devriez voir :
```
=== AuthInterceptor ===
Request URL: http://localhost:1111/api/campaigns
Request method: GET
Token present? true/false
Token value: eyJ... (ou null)
```

**Si `Token present? false` :**
- Le token n'est pas dans localStorage
- Se reconnecter via `/login`

### 3. Vérifier l'Onglet Network (F12 > Network)

1. Recharger la page `/campaigns`
2. Chercher la requête vers `/api/campaigns`
3. Vérifier :
   - **Status** : 200, 401, 403, 500, ou (failed) ?
   - **Type** : xhr, fetch, ou autre ?
   - **Headers Request** : Le header `Authorization: Bearer ...` est-il présent ?
   - **Headers Response** : Y a-t-il des headers CORS (`Access-Control-Allow-Origin`) ?

### 4. Tester avec une Requête Directe

Dans la console du navigateur :
```javascript
// Récupérer le token
const token = localStorage.getItem('auth_token');

// Tester la requête
fetch('http://localhost:1111/api/campaigns', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => {
  console.log('Response status:', response.status);
  console.log('Response headers:', response.headers);
  return response.json();
})
.then(data => console.log('Data:', data))
.catch(error => console.error('Error:', error));
```

**Résultat attendu :**
- Si succès : `Response status: 200` et les données
- Si erreur CORS : `CORS policy: No 'Access-Control-Allow-Origin'`
- Si erreur réseau : `Failed to fetch`

### 5. Vérifier que les Services sont Démarrés

#### Vérifier Eureka Dashboard
```
http://localhost:8761
```

**Services attendus :**
- ✅ `userservice` - Status: UP
- ✅ `campaignservice` - Status: UP ← **Vérifier celui-ci**
- ✅ `reactivegateway` - Status: UP

#### Vérifier les Ports
```bash
# PowerShell
netstat -ano | findstr :1111  # Gateway
netstat -ano | findstr :9020  # Campaign Service
```

## 🛠️ Solutions selon le Problème

### Problème 1 : Token Manquant
**Symptôme :** `Token present? false` dans les logs

**Solution :**
1. Aller sur `/login`
2. Se connecter avec un compte valide
3. Vérifier que le token est sauvegardé : `localStorage.getItem('auth_token')`
4. Recharger `/campaigns`

### Problème 2 : Erreur CORS
**Symptôme :** Erreur dans la console : `CORS policy: No 'Access-Control-Allow-Origin'`

**Solution :**
- La configuration CORS est déjà en place dans le gateway
- Vérifier que le gateway est bien démarré
- Vérifier que `CorsConfig.java` est bien chargé
- Vérifier que l'origine `http://localhost:4200` est bien autorisée

### Problème 3 : Service Non Démarré
**Symptôme :** `No servers available for service: campaignservice` dans les logs du gateway

**Solution :**
1. Démarrer le `campaignservice`
2. Vérifier qu'il s'enregistre dans Eureka
3. Vérifier les logs pour des erreurs de démarrage

### Problème 4 : Problème avec `withFetch()`
**Symptôme :** L'erreur persiste même avec un token valide

**Solution temporaire :** Retirer `withFetch()` pour tester avec XHR :
```typescript
// Dans app.config.ts
provideHttpClient(
  // withFetch(), // Commenter temporairement
  withInterceptors([authInterceptor])
)
```

## 📝 Informations à Fournir

Pour un diagnostic complet, fournir :

1. **Logs de la console du navigateur** (F12 > Console)
   - Tous les logs de `AuthInterceptor`
   - Tous les logs de `CampaignService`
   - Tous les logs d'erreur

2. **Détails de la requête HTTP** (F12 > Network > `/api/campaigns`)
   - Status code
   - Headers Request (surtout `Authorization`)
   - Headers Response (surtout CORS)
   - Response body (si disponible)

3. **Résultat de `localStorage.getItem('auth_token')`**
   - Token présent ou null ?

4. **Status dans Eureka Dashboard** (`http://localhost:8761`)
   - `campaignservice` est-il visible et UP ?

5. **Logs du gateway** (chercher "campaignservice" ou "CORS")

## 🎯 Prochaines Étapes

1. **Ouvrir la console du navigateur (F12)**
2. **Exécuter les tests ci-dessus**
3. **Partager les résultats pour diagnostic final**
