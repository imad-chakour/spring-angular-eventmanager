package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "User Service is running!";
    }

    @Retry(name = "userRetry", fallbackMethod = "fallbackUsersCB")
    @CircuitBreaker(name = "userCB", fallbackMethod = "fallbackUsersCB")
    @GetMapping
    public ResponseEntity<?> getUsers() {
        simulateRandomFailure();
        List<User> users = (List<User>) userService.getUsers();
        // Remove passwords from response
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
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

    private void simulateRandomFailure() {
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated random failure in User Service");
        }
    }

    public ResponseEntity<?> fallbackUsersCB(Exception e) {
        System.err.println("User Service Fallback: " + e.getMessage());
        User fallbackUser = new User(
                1L,
                "admin@eventflow.com",
                "Admin",
                "User",
                com.example.userservice.model.UserRole.ADMIN,
                com.example.userservice.model.UserStatus.ACTIVE
        );
        return ResponseEntity.ok(List.of(fallbackUser));
    }
}