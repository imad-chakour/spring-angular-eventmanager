# Solution Immédiate : Erreur 403 sur /api/users/register

## 🎯 Problème

L'endpoint direct fonctionne mais pas via le gateway. Cela indique que le **load balancer ne trouve pas le service dans Eureka**.

## ✅ Solution Rapide

### Option 1 : Vérifier Eureka (Recommandé)

1. **Ouvrir Eureka Dashboard** : `http://localhost:8761`
2. **Vérifier** que `userservice` est dans la liste avec statut `UP`
3. **Si absent** :
   - Vérifier que `userservice` est démarré
   - Vérifier les logs de `userservice` pour les erreurs Eureka
   - Attendre 30-60 secondes après démarrage

### Option 2 : Utiliser l'URL Directe Temporairement (Pour Test)

Modifier temporairement `GatewayRoutesConfig.java` :

```java
.uri("http://localhost:7020"))  // Au lieu de lb://userservice
```

**⚠️ À utiliser uniquement pour tester ! Remettre `lb://userservice` après.**

### Option 3 : Vérifier la Configuration Eureka

Dans `userservice/src/main/resources/application.properties`, ajouter :

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
eureka.instance.instance-id=${spring.application.name}:${server.port}
```

## 🔍 Diagnostic

### Vérifier les Logs du Gateway

Chercher dans les logs :
- `LoadBalancerClientFilter` - erreurs de service non trouvé
- `JWT Filter` - voir si l'endpoint est autorisé
- `Gateway Error` - erreurs détaillées

### Vérifier les Logs du User Service

Chercher :
- `DiscoveryClient` - enregistrement dans Eureka
- Erreurs de connexion à Eureka

## 📋 Checklist

- [ ] Eureka Server démarré (8761)
- [ ] User Service démarré (7020)
- [ ] User Service visible dans Eureka (http://localhost:8761)
- [ ] Gateway démarré (1111)
- [ ] Attendre 30 secondes après démarrage
- [ ] Tester à nouveau

## 🚀 Test Rapide

```bash
# Test direct (doit fonctionner)
curl -X POST http://localhost:7020/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123","role":"PARTICIPANT"}'

# Test via gateway (doit fonctionner après correction)
curl -X POST http://localhost:1111/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123","role":"PARTICIPANT"}'
```
