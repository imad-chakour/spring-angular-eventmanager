package com.example.userservice.controller;

import com.example.userservice.client.NotificationClient;
import com.example.userservice.model.User;
import com.example.userservice.security.JwtTokenProvider;
import com.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationClient notificationClient;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          NotificationClient notificationClient) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.notificationClient = notificationClient;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Email: " + email);
        System.out.println("Password provided: " + password);

        try {
            // First, check if user exists
            var userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                System.out.println("User not found in database");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password"));
            }

            User dbUser = userOpt.get();
            System.out.println("User found: " + dbUser.getEmail());
            System.out.println("DB Password hash: " + dbUser.getPassword());

            // Verify password before attempting authentication
            boolean passwordMatches = passwordEncoder.matches(password, dbUser.getPassword());
            System.out.println("Password verification: " + passwordMatches);
            
            if (!passwordMatches) {
                System.out.println("Password does not match for user: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password"));
            }

            // If password matches, proceed with authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            System.out.println("Authentication successful!");
            String token = jwtTokenProvider.generateToken(authentication);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("email", email);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            System.out.println("Authentication failed: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        // SECURITY: Force PARTICIPANT role for all registrations
        // Role assignment can only be done by admins via user management
        user.setRole(com.example.userservice.model.UserRole.PARTICIPANT);

        // Password encoding is handled by UserService.saveUser()
        // Do NOT encode here to avoid double encoding

        User saved = userService.saveUser(user);

        // Créer une notification de bienvenue
        try {
            Map<String, Object> welcomeNotification = new HashMap<>();
            welcomeNotification.put("recipientId", saved.getId());
            welcomeNotification.put("recipientEmail", saved.getEmail());
            welcomeNotification.put("type", "WELCOME");
            welcomeNotification.put("channel", "EMAIL");
            welcomeNotification.put("subject", "Bienvenue sur EventFlow !");
            welcomeNotification.put("content", "Bonjour " + (saved.getFirstName() != null ? saved.getFirstName() : saved.getEmail()) + 
                    ",\n\nBienvenue sur EventFlow ! Votre compte a été créé avec succès.\n\n" +
                    "Vous pouvez maintenant vous inscrire aux événements et participer à nos activités.\n\n" +
                    "Cordialement,\nL'équipe EventFlow");
            welcomeNotification.put("status", "PENDING");
            
            notificationClient.createNotification(welcomeNotification);
            System.out.println("=== Notification de bienvenue créée pour l'utilisateur: " + saved.getEmail() + " ===");
        } catch (Exception e) {
            // Ne pas faire échouer l'inscription si la notification échoue
            System.err.println("⚠️ Erreur lors de la création de la notification de bienvenue: " + e.getMessage());
        }

        // Remove password from response
        saved.setPassword(null);
        return ResponseEntity.ok(saved);
    }
}