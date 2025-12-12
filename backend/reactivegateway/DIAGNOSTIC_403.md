# Diagnostic : Erreur 403 sur /api/users/register via Gateway

## 🔍 Problème

- ✅ `http://localhost:7020/api/users/register` fonctionne (direct)
- ❌ `http://localhost:1111/api/users/register` retourne 403 (via gateway)

## 🔎 Causes Possibles

### 1. Service Non Trouvé dans Eureka (Le Plus Probable)

Le load balancer Spring Cloud (`lb://userservice`) ne trouve pas le service dans Eureka.

**Vérifications :**
1. Ouvrir `http://localhost:8761` (Eureka Dashboard)
2. Chercher `userservice` dans la liste des services
3. Vérifier que le statut est `UP`

**Si le service n'est pas visible :**
- Vérifier que `userservice` est démarré
- Vérifier la configuration Eureka dans `userservice`
- Vérifier que `eureka.client.service-url.defaultZone` est correct

### 2. Nom de Service Incorrect

Le gateway cherche `userservice` (minuscules) mais le service est peut-être enregistré différemment.

**Vérification :**
- Dans Eureka Dashboard, voir le nom exact du service
- Comparer avec `lb://userservice` dans `GatewayRoutesConfig.java`

### 3. Filtre JWT Bloque (Peu Probable)

Le filtre JWT devrait laisser passer `/api/users/register` car c'est dans `PUBLIC_ENDPOINTS`.

**Vérification :**
- Vérifier les logs du gateway pour voir si le filtre JWT s'exécute
- Chercher `"Allowing public endpoint: /api/users/register"` dans les logs

## 🔧 Solutions

### Solution 1 : Vérifier Eureka

1. Démarrer Eureka Server
2. Démarrer User Service
3. Attendre 30 secondes pour l'enregistrement
4. Vérifier dans Eureka Dashboard : `http://localhost:8761`
5. Redémarrer le Gateway

### Solution 2 : Utiliser l'URL Directe Temporairement

Pour tester, modifier temporairement la route :

```java
.uri("http://localhost:7020"))  // Au lieu de lb://userservice
```

**⚠️ Ne pas utiliser en production !**

### Solution 3 : Vérifier la Configuration Eureka

Dans `userservice/src/main/resources/application.properties` :

```properties
spring.application.name=userservice
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

### Solution 4 : Activer les Logs de Load Balancer

Les logs sont maintenant activés en DEBUG pour voir les erreurs du load balancer.

## 📋 Checklist de Diagnostic

- [ ] Eureka Server démarré (port 8761)
- [ ] User Service démarré (port 7020)
- [ ] User Service visible dans Eureka Dashboard
- [ ] Gateway démarré (port 1111)
- [ ] Gateway visible dans Eureka Dashboard
- [ ] Attendre 30 secondes après démarrage des services
- [ ] Vérifier les logs du Gateway pour les erreurs

## 🧪 Test de Diagnostic

### Test 1 : Vérifier Eureka
```
GET http://localhost:8761/eureka/apps
```

### Test 2 : Vérifier le Service Direct
```
POST http://localhost:7020/api/users/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123",
  "role": "PARTICIPANT"
}
```

### Test 3 : Vérifier via Gateway
```
POST http://localhost:1111/api/users/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123",
  "role": "PARTICIPANT"
}
```

## 📝 Logs à Vérifier

Dans les logs du Gateway, chercher :
- `LoadBalancerClientFilter` - pour voir si le service est trouvé
- `JWT Filter` - pour voir si l'endpoint est autorisé
- `Gateway Error` - pour voir les erreurs détaillées

## ✅ Solution Rapide

Si le service n'est pas dans Eureka :

1. **Vérifier la configuration Eureka dans userservice**
2. **Redémarrer userservice**
3. **Attendre 30 secondes**
4. **Vérifier dans Eureka Dashboard**
5. **Redémarrer le gateway**
