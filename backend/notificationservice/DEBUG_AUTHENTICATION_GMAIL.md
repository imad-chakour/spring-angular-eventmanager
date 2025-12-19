# 🔍 Debug : Erreur d'Authentification Gmail

## ❌ Erreur Rencontrée

```
MailAuthenticationException: Authentication failed
535-5.7.8 Username and Password not accepted
```

## 🔍 Causes Possibles

### 1. Variable MAIL_PASSWORD Non Définie ou Vide

**Vérification :**
```powershell
echo $env:MAIL_PASSWORD
```

**Si rien n'est affiché :** La variable n'est pas définie.

**Solution :**
```powershell
$env:MAIL_PASSWORD="votre-mot-de-passe-application"
```

### 2. Utilisation du Mot de Passe Gmail au Lieu du Mot de Passe d'Application

**❌ Incorrect :** Utiliser votre mot de passe Gmail normal
**✅ Correct :** Utiliser un "mot de passe d'application" (16 caractères)

**Comment obtenir un mot de passe d'application :**
1. Aller sur : https://myaccount.google.com/security
2. Activer "Validation en deux étapes" (obligatoire)
3. Cliquer sur "Mots de passe des applications"
4. Sélectionner "Mail" et "Autre (nom personnalisé)" → "EventFlow"
5. Copier le mot de passe généré (16 caractères, format : `xxxx xxxx xxxx xxxx`)

### 3. Authentification à Deux Facteurs Non Activée

**Vérification :**
- Aller sur : https://myaccount.google.com/security
- Vérifier que "Validation en deux étapes" est activée

**Si non activée :** Les mots de passe d'application ne peuvent pas être générés.

### 4. Mot de Passe d'Application Révoqué ou Expiré

**Solution :** Générer un nouveau mot de passe d'application.

### 5. Espaces dans le Mot de Passe

**Format du mot de passe d'application :** `xxxx xxxx xxxx xxxx` (avec espaces)

**Dans PowerShell, utiliser des guillemets :**
```powershell
$env:MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
```

## ✅ Solution Étape par Étape

### Étape 1 : Vérifier l'Authentification à Deux Facteurs

1. Aller sur : https://myaccount.google.com/security
2. Vérifier que "Validation en deux étapes" est **activée**
3. Si non activée, l'activer d'abord

### Étape 2 : Générer un Mot de Passe d'Application

1. Sur la page Sécurité, cliquer sur **"Mots de passe des applications"**
2. Si vous ne voyez pas cette option, l'authentification à deux facteurs n'est pas activée
3. Sélectionner :
   - **Application :** Mail
   - **Appareil :** Autre (nom personnalisé) → Entrer "EventFlow"
4. Cliquer sur **"Générer"**
5. **Copier le mot de passe** (16 caractères, format : `xxxx xxxx xxxx xxxx`)

### Étape 3 : Définir la Variable d'Environnement

**PowerShell :**
```powershell
$env:MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
```

**Important :**
- Utiliser des guillemets si le mot de passe contient des espaces
- Le mot de passe doit être exactement celui généré (16 caractères)

### Étape 4 : Vérifier la Variable

```powershell
echo $env:MAIL_PASSWORD
```

**Doit afficher :** Le mot de passe (16 caractères)

### Étape 5 : Redémarrer le NotificationService

**Important :** Le service doit être démarré **après** avoir défini la variable.

### Étape 6 : Tester

Créer un compte et vérifier les logs :
```
✅ Email envoyé avec succès à: imad.chakour@uit.ac.ma
```

## 🧪 Test de Configuration

### Vérifier que JavaMailSender est Configuré

Si vous voyez dans les logs :
```
JavaMailSender n'est pas configuré. Email non envoyé (simulation).
```
→ La variable `MAIL_PASSWORD` n'est pas définie ou est vide.

### Vérifier l'Authentification

Si vous voyez :
```
MailAuthenticationException: Authentication failed
```
→ Le mot de passe est incorrect ou n'est pas un mot de passe d'application.

## 📝 Checklist de Vérification

- [ ] Authentification à deux facteurs activée sur Gmail
- [ ] Mot de passe d'application généré (16 caractères)
- [ ] Variable `MAIL_PASSWORD` définie dans PowerShell
- [ ] Variable vérifiée avec `echo $env:MAIL_PASSWORD`
- [ ] NotificationService redémarré **après** définition de la variable
- [ ] Logs montrent "Email envoyé avec succès" (pas d'erreur d'authentification)

## 🔧 Configuration Alternative : Fichier application.properties

Si les variables d'environnement ne fonctionnent pas, vous pouvez mettre directement dans `application.properties` :

```properties
spring.mail.password=votre-mot-de-passe-application-16-caracteres
```

⚠️ **Attention :** Ne commitez JAMAIS ce fichier dans Git avec le mot de passe !

## 💡 Astuce

Pour éviter de redéfinir la variable à chaque fois, vous pouvez créer un script PowerShell :

**`start-notificationservice.ps1` :**
```powershell
$env:MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
cd "C:\Users\chako\OneDrive\Desktop\5iir\spring boot\myapp_spring_boot_angular\backend\notificationservice"
mvn spring-boot:run
```

Puis exécuter : `.\start-notificationservice.ps1`

