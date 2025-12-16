# 📝 Créer des Événements via Postman

## Option 1 : Insérer via SQL (Recommandé)

Exécuter le script SQL `INSERT_TEST_EVENTS.sql` dans votre base de données Oracle.

## Option 2 : Créer via l'API POST

### Endpoint
```
POST http://localhost:1111/api/events
Authorization: Bearer <token>
Content-Type: application/json
```

### Body JSON - Exemple 1 : Webinaire
```json
{
  "title": "Webinaire Introduction Spring Boot",
  "description": "Un webinaire pour découvrir les bases de Spring Boot",
  "type": "WEBINAIRE",
  "format": "VIRTUEL",
  "startDate": "2025-12-20T10:00:00",
  "endDate": "2025-12-20T11:00:00",
  "location": "Online",
  "maxCapacity": 100,
  "currentParticipants": 0,
  "status": "PLANIFIED",
  "organizerId": 1
}
```

### Body JSON - Exemple 2 : Conférence
```json
{
  "title": "Conférence Microservices Architecture",
  "description": "Conférence sur l'architecture microservices avec Spring Cloud",
  "type": "CONFERENCE",
  "format": "PHYSIQUE",
  "startDate": "2025-12-25T14:00:00",
  "endDate": "2025-12-25T17:00:00",
  "location": "Paris, France",
  "maxCapacity": 50,
  "currentParticipants": 0,
  "status": "ACTIF",
  "organizerId": 1
}
```

### Body JSON - Exemple 3 : Atelier
```json
{
  "title": "Atelier Angular Avancé",
  "description": "Atelier pratique sur les fonctionnalités avancées d'Angular",
  "type": "ATELIER",
  "format": "HYBRIDE",
  "startDate": "2026-01-05T09:00:00",
  "endDate": "2026-01-05T13:00:00",
  "location": "Lyon, France / Online",
  "maxCapacity": 30,
  "currentParticipants": 5,
  "status": "ACTIF",
  "organizerId": 2
}
```

## Types d'Événements Disponibles

- `WEBINAIRE`
- `SALON`
- `PROMOTION`
- `CONFERENCE`
- `ATELIER`

## Formats Disponibles

- `VIRTUEL`
- `PHYSIQUE`
- `HYBRIDE`

## Statuts Disponibles

- `PLANIFIED`
- `ACTIF`
- `TERMINE`
- `ANNULE`
- `CLOTURE`

## Notes

- `organizerId` doit correspondre à un utilisateur existant dans le `userservice`
- `eventId` sera généré automatiquement (format : `EVT-XXXXXXXX`)
- `createdAt` et `updatedAt` seront générés automatiquement
- Les dates doivent être au format ISO 8601 : `YYYY-MM-DDTHH:mm:ss`

## Vérification

Après création, tester :
```
GET http://localhost:1111/api/events
Authorization: Bearer <token>
```

Vous devriez voir les événements créés dans la réponse.
