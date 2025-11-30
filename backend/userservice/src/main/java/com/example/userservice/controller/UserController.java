package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.model.UserRole;
import com.example.userservice.model.UserStatus;
import com.example.userservice.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
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
    public Iterable<User> getUsers() {
        simulateRandomFailure();
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") final Long id) {
        return userService.getUser(id).orElse(null);
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable("email") final String email) {
        return userService.getUserByEmail(email).orElse(null);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") final Long id, @RequestBody User user) {
        return userService.getUser(id).map(existing -> {
            existing.setFirstName(user.getFirstName());
            existing.setLastName(user.getLastName());
            existing.setEmail(user.getEmail());
            existing.setRole(user.getRole());
            existing.setStatus(user.getStatus());
            return userService.saveUser(existing);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") final Long id) {
        userService.deleteUser(id);
    }

    @PatchMapping("/{id}/last-login")
    public void updateLastLogin(@PathVariable("id") final Long id) {
        userService.updateLastLogin(id);
    }

    private void simulateRandomFailure() {
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated random failure in User Service");
        }
    }

    public Iterable<User> fallbackUsersCB(Exception e) {
        System.err.println("User Service Fallback: " + e.getMessage());
        return List.of(
                new User(1L, "admin@eventflow.com", "Admin", "User", "admin", UserRole.ADMIN, UserStatus.ACTIVE, null, null, null)
        );
    }
}