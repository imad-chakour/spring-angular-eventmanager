# Guide de Dépannage - Reactive Gateway

## Erreur "Failed to fetch"

### Causes possibles :

1. **Gateway non démarré**
   - Vérifier que le gateway est démarré sur le port 1111
   - Tester : `curl http://localhost:1111/actuator/health`

2. **Problème CORS**
   - Vérifier que `CorsConfig` est bien chargé
   - Les requêtes OPTIONS doivent passer sans JWT
   - Vérifier les headers dans la console du navigateur (F12 → Network)

3. **Filtre JWT bloque les requêtes**
   - Les endpoints publics (`/api/users/login`, `/api/users/register`) doivent passer
   - Les requêtes OPTIONS doivent passer

4. **Eureka non démarré**
   - Le gateway a besoin d'Eureka pour découvrir les services
   - Vérifier : `http://localhost:8761`

### Solutions :

#### 1. Vérifier que le gateway est démarré
```bash
curl http://localhost:1111/actuator/health
```

#### 2. Vérifier les logs du gateway
Chercher les erreurs dans les logs au démarrage.

#### 3. Tester directement l'endpoint
```bash
curl -X POST http://localhost:1111/api/users/register \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:4200" \
  -d '{"email":"test@test.com","password":"password123","role":"PARTICIPANT"}'
```

#### 4. Vérifier la console du navigateur
- Ouvrir F12 → Network
- Voir la requête qui échoue
- Vérifier les headers de réponse
- Vérifier les erreurs CORS

#### 5. Vérifier l'ordre des filtres
- CORS doit être exécuté en premier
- JWT doit laisser passer OPTIONS et les endpoints publics

### Configuration CORS

Le filtre CORS doit être configuré avec :
- `allowedOrigins`: `http://localhost:4200`
- `allowedMethods`: Toutes les méthodes
- `allowedHeaders`: `*`
- `allowCredentials`: `true`

### Endpoints publics

Les endpoints suivants ne nécessitent pas de JWT :
- `/api/users/login`
- `/api/users/register`
- `/actuator/**`

### Ordre des filtres

1. CORS (automatique via `CorsWebFilter`)
2. JWT Authentication Filter (Order: -50)
3. Logging Filter (Order: HIGHEST_PRECEDENCE + 1)
4. Autres filtres
