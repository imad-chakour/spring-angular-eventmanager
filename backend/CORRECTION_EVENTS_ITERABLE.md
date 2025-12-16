# ✅ Correction : Events - Problème de Sérialisation JSON

## ❌ Problème Identifié

L'endpoint `GET http://localhost:1111/api/events` retournait un objet avec tous les champs à `null` :
```json
[
    {
        "id": null,
        "eventId": null,
        "title": null,
        "description": null,
        "type": null,
        "format": null,
        "startDate": null,
        "endDate": null,
        "location": null,
        "maxCapacity": null,
        "currentParticipants": 0,
        "status": null,
        "organizerId": null,
        "createdAt": null,
        "updatedAt": null
    }
]
```

Même si la base de données contient des données.

## 🔍 Cause

Le contrôleur retournait `Iterable<Event>` au lieu de `List<Event>`. Spring peut avoir des problèmes de sérialisation JSON avec `Iterable`, surtout avec des entités JPA.

## ✅ Solution Appliquée

### Principe
Convertir l'`Iterable` en `List` avant de retourner la réponse, comme cela a été fait pour les campagnes.

### Corrections

#### 1. Endpoint `getEvents()`
**Avant :**
```java
@GetMapping
public Iterable<Event> getEvents() {
    simulateRandomFailure();
    return eventService.getEvents();
}
```

**Après :**
```java
@GetMapping
public List<Event> getEvents() {
    simulateRandomFailure();
    Iterable<Event> events = eventService.getEvents();
    return StreamSupport.stream(events.spliterator(), false)
            .collect(Collectors.toList());
}
```

#### 2. Endpoint `getEventsByOrganizer()`
**Avant :**
```java
@GetMapping("/organizer/{organizerId}")
public Iterable<Event> getEventsByOrganizer(@PathVariable("organizerId") final Long organizerId) {
    return eventService.getEventsByOrganizer(organizerId);
}
```

**Après :**
```java
@GetMapping("/organizer/{organizerId}")
public List<Event> getEventsByOrganizer(@PathVariable("organizerId") final Long organizerId) {
    Iterable<Event> events = eventService.getEventsByOrganizer(organizerId);
    return StreamSupport.stream(events.spliterator(), false)
            .collect(Collectors.toList());
}
```

#### 3. Endpoint `getEventsByStatus()`
**Avant :**
```java
@GetMapping("/status/{status}")
public Iterable<Event> getEventsByStatus(@PathVariable("status") final EventStatus status) {
    return eventService.getEventsByStatus(status);
}
```

**Après :**
```java
@GetMapping("/status/{status}")
public List<Event> getEventsByStatus(@PathVariable("status") final EventStatus status) {
    Iterable<Event> events = eventService.getEventsByStatus(status);
    return StreamSupport.stream(events.spliterator(), false)
            .collect(Collectors.toList());
}
```

#### 4. Endpoints de Registration
Tous les endpoints de registration ont également été corrigés pour retourner `List<Registration>` au lieu de `Iterable<Registration>` :
- `getRegistrations()`
- `getRegistrationsByEvent()`
- `getRegistrationsByUser()`

#### 5. Imports Ajoutés
```java
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
```

## 📋 Pattern à Suivre

Pour tous les endpoints qui retournent des collections depuis un repository JPA :

**❌ Ne pas faire :**
```java
@GetMapping
public Iterable<Event> getEvents() {
    return eventRepository.findAll();
}
```

**✅ Faire :**
```java
@GetMapping
public List<Event> getEvents() {
    Iterable<Event> events = eventRepository.findAll();
    return StreamSupport.stream(events.spliterator(), false)
            .collect(Collectors.toList());
}
```

## 🚀 Action Requise

**Redémarrer le EventService** (port 9030) pour appliquer les changements.

## ✅ Vérification

Après redémarrage, tester :
```bash
GET http://localhost:1111/api/events
Authorization: Bearer <token>
```

**Résultat attendu :**
- ✅ Liste d'événements avec tous les champs remplis
- ✅ Pas de champs à `null` (sauf si vraiment null en base)
- ✅ Format JSON correct

## 📝 Fichiers Modifiés

- `backend/eventservice/src/main/java/com/example/event_service/controller/EventController.java`
  - Conversion de tous les `Iterable` en `List`
  - Ajout des imports `Collectors` et `StreamSupport`

## 🎯 Règle Générale

**Dans les contrôleurs REST Spring Boot :**
- ✅ Toujours retourner `List<T>` au lieu de `Iterable<T>`
- ✅ Convertir l'`Iterable` du repository en `List` avant de retourner
- ✅ Cela garantit une sérialisation JSON correcte

Cette correction garantit que Spring sérialise correctement les entités JPA en JSON.
