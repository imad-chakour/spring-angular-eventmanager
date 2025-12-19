package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
// CORS is handled by the Gateway (CorsConfig), no need for @CrossOrigin here
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "User Service is running!";
    }

    @GetMapping
    public ResponseEntity<?> getUsers() {
        System.out.println("=== UserController.getUsers() appelé ===");
        System.out.println("=== Timestamp: " + System.currentTimeMillis() + " ===");
        
        Iterable<User> usersIterable = userService.getUsers();
        // Convert Iterable to List for proper JSON serialization
        List<User> users = new java.util.ArrayList<>();
        usersIterable.forEach(users::add);
        
        System.out.println("=== Nombre d'utilisateurs dans la réponse: " + users.size() + " ===");
        users.forEach(user -> {
            System.out.println("  - User ID: " + user.getId() + ", Email: " + user.getEmail() + ", Role: " + user.getRole());
            user.setPassword(null);
        });
        
        // Désactiver le cache HTTP pour forcer le rafraîchissement
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") final Long id) {
        return userService.getUser(id)
                .map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") final String email) {
        return userService.getUserByEmail(email)
                .map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User saved = userService.saveUser(user);
        saved.setPassword(null);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") final Long id, @RequestBody User user) {
        return userService.getUser(id)
                .map(existing -> {
                    existing.setFirstName(user.getFirstName());
                    existing.setLastName(user.getLastName());
                    existing.setEmail(user.getEmail());
                    existing.setRole(user.getRole());
                    existing.setStatus(user.getStatus());
                    // Marketing fields
                    existing.setPhone(user.getPhone());
                    existing.setCompany(user.getCompany());
                    existing.setJobTitle(user.getJobTitle());
                    existing.setSegments(user.getSegments());
                    existing.setCommunicationPreferences(user.getCommunicationPreferences());
                    existing.setOptInMarketing(user.getOptInMarketing());
                    existing.setLastActivity(user.getLastActivity());
                    User updated = userService.saveUser(existing);
                    updated.setPassword(null);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") final Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/last-login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable("id") final Long id) {
        userService.updateLastLogin(id);
        return ResponseEntity.ok().build();
    }

}