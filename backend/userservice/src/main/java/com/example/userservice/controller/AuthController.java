package com.example.userservice.controller;

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

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
                        .body(Map.of("error", "User not found"));
            }

            User dbUser = userOpt.get();
            System.out.println("User found: " + dbUser.getEmail());
            System.out.println("DB Password hash: " + dbUser.getPassword());

            // Try manual password check
            boolean passwordMatches = passwordEncoder.matches(password, dbUser.getPassword());
            System.out.println("Manual password check: " + passwordMatches);

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
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials", "details", ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(com.example.userservice.model.UserRole.PARTICIPANT);
        }

        User saved = userService.saveUser(user);

        // Remove password from response
        saved.setPassword(null);
        return ResponseEntity.ok(saved);
    }
}