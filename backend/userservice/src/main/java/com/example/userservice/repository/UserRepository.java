package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Requête native pour forcer une lecture directe depuis la base (bypass cache)
    @Query(value = "SELECT * FROM users ORDER BY id", nativeQuery = true)
    List<User> findAllFresh();
}