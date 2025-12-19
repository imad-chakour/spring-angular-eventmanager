package com.example.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service pour l'envoi d'emails
 */
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    /**
     * Envoie un email
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param content Contenu de l'email
     * @return true si l'email a été envoyé avec succès, false sinon
     */
    public boolean sendEmail(String to, String subject, String content) {
        if (mailSender == null) {
            System.out.println("JavaMailSender n'est pas configuré. Email non envoyé (simulation).");
            System.out.println("   To: " + to);
            System.out.println("   Subject: " + subject);
            System.out.println("   Content: " + content.substring(0, Math.min(100, content.length())) + "...");
            return true; // Retourne true pour simuler l'envoi réussi
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom("noreply@eventflow.com"); // Peut être configuré dans application.properties

            System.out.println("=== Tentative d'envoi d'email ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("From: noreply@eventflow.com");
            
            mailSender.send(message);
            System.out.println("✅ Email envoyé avec succès à: " + to);
            return true;
        } catch (org.springframework.mail.MailAuthenticationException e) {
            System.err.println("❌ Erreur d'authentification Gmail:");
            System.err.println("   - Vérifiez que MAIL_PASSWORD est défini (mot de passe d'application Gmail)");
            System.err.println("   - Vérifiez que l'authentification à deux facteurs est activée");
            System.err.println("   - Vérifiez que vous utilisez un 'mot de passe d'application' (16 caractères), pas votre mot de passe Gmail");
            System.err.println("   - Message: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email à " + to + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

