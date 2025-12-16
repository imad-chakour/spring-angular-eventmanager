-- Script SQL pour insérer des événements de test
-- À exécuter dans la base de données Oracle

-- Vérifier d'abord si la séquence existe
-- Si elle n'existe pas, la créer :
-- CREATE SEQUENCE EVENTS_SEQ START WITH 1 INCREMENT BY 1;

-- Insérer des événements de test avec NEXTVAL pour l'ID généré
INSERT INTO events (
    id,
    event_id,
    title,
    description,
    type,
    format,
    start_date,
    end_date,
    location,
    max_capacity,
    current_participants,
    status,
    organizer_id,
    created_at,
    updated_at
) VALUES (
             EVENTS_SEQ.NEXTVAL,
             'EVT-001',
             'Webinaire Introduction Spring Boot',
             'Un webinaire pour découvrir les bases de Spring Boot et son écosystème',
             'WEBINAIRE',
             'VIRTUEL',
             SYSDATE + 1,
             SYSDATE + 1 + (1/24),
             'Online',
             100,
             0,
             'PLANIFIED',
             1,
             SYSDATE,
             SYSDATE
         );

INSERT INTO events (
    id,
    event_id,
    title,
    description,
    type,
    format,
    start_date,
    end_date,
    location,
    max_capacity,
    current_participants,
    status,
    organizer_id,
    created_at,
    updated_at
) VALUES (
             EVENTS_SEQ.NEXTVAL,
             'EVT-002',
             'Conférence Microservices Architecture',
             'Conférence sur l''architecture microservices avec Spring Cloud',
             'CONFERENCE',
             'PHYSIQUE',
             SYSDATE + 7,
             SYSDATE + 7 + (3/24),
             'Paris, France',
             50,
             0,
             'ACTIF',
             1,
             SYSDATE,
             SYSDATE
         );

INSERT INTO events (
    id,
    event_id,
    title,
    description,
    type,
    format,
    start_date,
    end_date,
    location,
    max_capacity,
    current_participants,
    status,
    organizer_id,
    created_at,
    updated_at
) VALUES (
             EVENTS_SEQ.NEXTVAL,
             'EVT-003',
             'Atelier Angular Avancé',
             'Atelier pratique sur les fonctionnalités avancées d''Angular',
             'ATELIER',
             'HYBRIDE',
             SYSDATE + 14,
             SYSDATE + 14 + (4/24),
             'Lyon, France / Online',
             30,
             5,
             'ACTIF',
             2,
             SYSDATE,
             SYSDATE
         );

INSERT INTO events (
    id,
    event_id,
    title,
    description,
    type,
    format,
    start_date,
    end_date,
    location,
    max_capacity,
    current_participants,
    status,
    organizer_id,
    created_at,
    updated_at
) VALUES (
             EVENTS_SEQ.NEXTVAL,
             'EVT-004',
             'Salon du Développement Web',
             'Salon annuel réunissant les développeurs web',
             'SALON',
             'PHYSIQUE',
             SYSDATE + 30,
             SYSDATE + 32,
             'Bordeaux, France',
             200,
             0,
             'PLANIFIED',
             2,
             SYSDATE,
             SYSDATE
         );

INSERT INTO events (
    id,
    event_id,
    title,
    description,
    type,
    format,
    start_date,
    end_date,
    location,
    max_capacity,
    current_participants,
    status,
    organizer_id,
    created_at,
    updated_at
) VALUES (
             EVENTS_SEQ.NEXTVAL,
             'EVT-005',
             'Promotion Nouveaux Produits',
             'Événement promotionnel pour présenter les nouveaux produits',
             'PROMOTION',
             'VIRTUEL',
             SYSDATE + 3,
             SYSDATE + 3 + (2/24),
             'Online',
             500,
             150,
             'ACTIF',
             3,
             SYSDATE,
             SYSDATE
         );

-- Vérifier les données insérées
SELECT * FROM events ORDER BY created_at DESC;