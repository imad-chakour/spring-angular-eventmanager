# 📧 Configuration Gmail pour l'Envoi d'Emails

## 📋 Étapes pour Configurer Gmail SMTP

### Étape 1 : Activer l'Authentification à Deux Facteurs

1. **Aller sur votre compte Google** : https://myaccount.google.com/
2. **Cliquer sur "Sécurité"** dans le menu de gauche
3. **Dans la section "Connexion à Google"**, trouver **"Validation en deux étapes"**
4. **Cliquer sur "Validation en deux étapes"** et suivre les instructions pour l'activer
   - Vous devrez confirmer votre numéro de téléphone
   - Entrer le code de vérification reçu

### Étape 2 : Générer un Mot de Passe d'Application

1. **Retourner sur la page Sécurité** : https://myaccount.google.com/security
2. **Dans la section "Validation en deux étapes"**, trouver **"Mots de passe des applications"**
3. **Cliquer sur "Mots de passe des applications"**
4. **Sélectionner l'application** : Choisir "Mail"
5. **Sélectionner l'appareil** : Choisir "Autre (nom personnalisé)" et entrer "EventFlow Notification Service"
6. **Cliquer sur "Générer"**
7. **Copier le mot de passe généré** (16 caractères, sans espaces)
   - ⚠️ **Important** : Ce mot de passe ne sera affiché qu'une seule fois, notez-le !

### Étape 3 : Configurer dans l'Application

#### Option A : Variables d'Environnement (Recommandé)

**Windows PowerShell :**
```powershell
$env:MAIL_USERNAME="votre-email@gmail.com"
$env:MAIL_PASSWORD="votre-mot-de-passe-application-16-caracteres"
```

**Windows CMD :**
```cmd
set MAIL_USERNAME=votre-email@gmail.com
set MAIL_PASSWORD=votre-mot-de-passe-application-16-caracteres
```

**Linux/Mac :**
```bash
export MAIL_USERNAME="votre-email@gmail.com"
export MAIL_PASSWORD="votre-mot-de-passe-application-16-caracteres"
```

#### Option B : Fichier application.properties

**Fichier :** `backend/notificationservice/src/main/resources/application.properties`

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-application-16-caracteres
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=noreply@eventflow.com
```

⚠️ **Attention** : Ne commitez JAMAIS le fichier `application.properties` avec le mot de passe dans Git !

### Étape 4 : Redémarrer le Service

1. **Arrêter le NotificationService** (port 7010)
2. **Redémarrer le NotificationService**
3. **Vérifier les logs** pour confirmer que JavaMailSender est configuré

### Étape 5 : Tester

**Créer un compte utilisateur :**
```bash
POST http://localhost:1111/api/users/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```

**Vérifier les logs :**
```
Email envoyé avec succès à: test@example.com
✅ Email envoyé et notification mise à jour: X
```

**Vérifier la boîte email** : L'email de bienvenue doit arriver dans quelques secondes.

## 🔒 Sécurité

### ⚠️ Ne JAMAIS :
- ❌ Commiter le mot de passe dans Git
- ❌ Partager le mot de passe d'application
- ❌ Utiliser votre mot de passe Gmail principal

### ✅ Bonnes Pratiques :
- ✅ Utiliser des variables d'environnement
- ✅ Utiliser un compte Gmail dédié pour les notifications
- ✅ Régénérer le mot de passe d'application si compromis
- ✅ Utiliser `.gitignore` pour exclure `application.properties` avec mots de passe

## 🐛 Dépannage

### Erreur : "Username and Password not accepted"

**Causes possibles :**
1. Le mot de passe d'application est incorrect
2. L'authentification à deux facteurs n'est pas activée
3. Le mot de passe d'application a été révoqué

**Solution :**
1. Vérifier que l'authentification à deux facteurs est activée
2. Générer un nouveau mot de passe d'application
3. Vérifier que vous utilisez le mot de passe d'application (16 caractères), pas votre mot de passe Gmail

### Erreur : "Could not connect to SMTP host"

**Causes possibles :**
1. Problème de connexion réseau
2. Port bloqué par le firewall
3. Configuration SMTP incorrecte

**Solution :**
1. Vérifier la connexion Internet
2. Vérifier que le port 587 n'est pas bloqué
3. Vérifier la configuration dans `application.properties`

### Email non reçu

**Vérifications :**
1. Vérifier les logs du service (erreurs d'envoi ?)
2. Vérifier le dossier Spam
3. Vérifier que l'adresse email est correcte
4. Vérifier que JavaMailSender est bien configuré (pas de message de simulation)

## 📝 Exemple de Configuration Complète

### application.properties (local, non commité)

```properties
spring.application.name=notificationservice
server.port=7010

spring.cloud.config.enabled=true
spring.config.import=optional:configserver:http://localhost:8888

# Spring Batch Configuration
spring.batch.job.enabled=false
spring.batch.jdbc.initialize-schema=always

# Spring Mail Configuration - Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=eventflow.notifications@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=noreply@eventflow.com
```

### Variables d'Environnement (Production)

```bash
export MAIL_USERNAME="eventflow.notifications@gmail.com"
export MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
```

## ✅ Vérification

Après configuration, les logs doivent montrer :
```
Email envoyé avec succès à: aminechakour03@gmail.com
✅ Email envoyé et notification mise à jour: 4
```

Au lieu de :
```
JavaMailSender n'est pas configuré. Email non envoyé (simulation).
```

## 🎯 Résumé Rapide

1. ✅ Activer l'authentification à deux facteurs sur Gmail
2. ✅ Générer un mot de passe d'application (16 caractères)
3. ✅ Configurer `MAIL_USERNAME` et `MAIL_PASSWORD` (variables d'environnement ou application.properties)
4. ✅ Redémarrer le NotificationService
5. ✅ Tester en créant un compte
6. ✅ Vérifier la réception de l'email

