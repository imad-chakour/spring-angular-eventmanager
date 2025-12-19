package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.model.UserStatus;
import com.example.userservice.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> getUser(final Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Iterable<User> getUsers() {
        System.out.println("=== UserService.getUsers() appelé ===");
        System.out.println("=== Timestamp: " + System.currentTimeMillis() + " ===");
        
        // Forcer une requête SQL fraîche en vidant le cache de session Hibernate
        entityManager.clear();
        
        // Utiliser une requête native pour forcer une lecture directe depuis la base (bypass cache Hibernate)
        List<User> users = userRepository.findAllFresh();
        
        System.out.println("=== Nombre d'utilisateurs récupérés depuis la base: " + users.size() + " ===");
        if (!users.isEmpty()) {
            // Afficher les 5 premiers pour debug
            for (int i = 0; i < Math.min(5, users.size()); i++) {
                User u = users.get(i);
                System.out.println("  User[" + i + "]: ID=" + u.getId() + ", Email=" + u.getEmail() + ", Role=" + u.getRole() + ", Status=" + u.getStatus());
            }
        } else {
            System.out.println("⚠️ Aucun utilisateur trouvé dans la base de données !");
        }
        
        return users;
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