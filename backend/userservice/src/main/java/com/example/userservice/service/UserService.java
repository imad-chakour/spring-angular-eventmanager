package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.model.UserStatus;
import com.example.userservice.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> getUser(final Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(final Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(UserStatus.INACTIVE);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    public User saveUser(User user) {
        if (user.getId() == null) {
            // New user - encode password
            user.setCreatedAt(LocalDateTime.now());
            if (user.getPassword() != null && !isPasswordEncoded(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            // Update existing user - only encode if password is not already encoded
            if (user.getPassword() != null && !isPasswordEncoded(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Check if password is already BCrypt encoded (starts with $2a$, $2b$, or $2y$)
     */
    private boolean isPasswordEncoded(String password) {
        return password != null && (password.startsWith("$2a$") || 
                                    password.startsWith("$2b$") || 
                                    password.startsWith("$2y$"));
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateLastLogin(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }
}