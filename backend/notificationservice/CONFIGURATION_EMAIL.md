# 📧 Configuration de l'Envoi d'Emails

## ✅ Implémentation

### Service d'Envoi d'Email

**Fichier :** `EmailService.java`

- ✅ Envoie les emails immédiatement lors de la création d'une notification
- ✅ Met à jour le statut à `SENT` après envoi réussi
- ✅ Enregistre la date d'envoi
- ✅ Gestion d'erreur : si l'envoi échoue, le statut reste `PENDING` pour retry

### Envoi Automatique

**Fichier :** `NotificationService.saveNotification()`

- ✅ Lors de la sauvegarde d'une notification avec canal `EMAIL`
- ✅ L'email est envoyé **immédiatement**
- ✅ Le statut passe à `SENT` si l'envoi réussit
- ✅ Sinon, reste `PENDING` pour retry via batch job

## ⚙️ Configuration SMTP

### Option 1 : Gmail (Recommandé pour les tests)

**Dans `application.properties` ou variables d'environnement :**

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-application
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=noreply@eventflow.com
```

**Pour Gmail, vous devez :**
1. Activer l'authentification à deux facteurs
2. Générer un "Mot de passe d'application" :
   - Aller dans : Compte Google → Sécurité → Mots de passe des applications
   - Créer un mot de passe pour "Mail"
   - Utiliser ce mot de passe dans la configuration

### Option 2 : Variables d'Environnement

```bash
export MAIL_USERNAME=votre-email@gmail.com
export MAIL_PASSWORD=votre-mot-de-passe-application
```

### Option 3 : Autre Serveur SMTP

```properties
spring.mail.host=smtp.votre-serveur.com
spring.mail.port=587
spring.mail.username=votre-username
spring.mail.password=votre-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## 🔄 Fonctionnement

### 1. Création de Notification

```
Notification créée (PENDING)
    ↓
EmailService.sendEmail() appelé immédiatement
    ↓
Si succès → Statut = SENT, sentDate = maintenant
Si échec → Statut = PENDING (pour retry)
```

### 2. Batch Job (Retry)

```
POST /api/notifications/batch/process
    ↓
Lit toutes les notifications PENDING
    ↓
EmailService.sendEmail() pour chaque notification
    ↓
Si succès → Statut = SENT
Si échec → Retry count++, si >= 3 → Statut = FAILED
```

## 🧪 Test sans Configuration SMTP

Si `JavaMailSender` n'est pas configuré :
- ✅ Le service **simule** l'envoi (logs dans la console)
- ✅ Retourne `true` (succès simulé)
- ✅ La notification passe à `SENT`
- ⚠️ **Aucun email réel n'est envoyé**

**Logs :**
```
⚠️ JavaMailSender n'est pas configuré. Email non envoyé (simulation).
   To: aminechakour03@gmail.com
   Subject: Bienvenue sur EventFlow !
   Content: Bonjour...
```

## 📝 Fichiers Modifiés

1. **`pom.xml`**
   - Ajout de `spring-boot-starter-mail`

2. **`EmailService.java`** (nouveau)
   - Service d'envoi d'email avec gestion d'erreur

3. **`NotificationService.java`**
   - Envoi automatique d'email lors de la sauvegarde

4. **`NotificationBatchConfig.java`**
   - Envoi réel d'email dans le batch job (au lieu de simulation)

5. **`application.properties`**
   - Configuration SMTP (Gmail par défaut)

## 🚀 Pour Activer l'Envoi Réel d'Emails

1. **Configurer SMTP** dans `application.properties` ou variables d'environnement
2. **Redémarrer le NotificationService**
3. **Tester** en créant un compte ou en s'inscrivant à un événement

## ✅ Vérification

Après configuration SMTP, les logs doivent montrer :
```
✅ Email envoyé avec succès à: aminechakour03@gmail.com
✅ Email envoyé et notification mise à jour: 4
```

Au lieu de :
```
⚠️ JavaMailSender n'est pas configuré. Email non envoyé (simulation).
```

