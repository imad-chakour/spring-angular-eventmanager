package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USERS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    // Marketing fields (previously in Participant)
    @Column(name = "phone")
    private String phone;

    @Column(name = "company")
    private String company;

    @Column(name = "job_title")
    private String jobTitle;

    @ElementCollection
    @CollectionTable(name = "user_segments", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "segment_name")
    private List<String> segments = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "preference_value")
    private List<String> communicationPreferences = new ArrayList<>();

    @Column(name = "opt_in_marketing")
    private Boolean optInMarketing = true;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
    }

    public User(Long id, String email, String firstName, String lastName, UserRole role, UserStatus status) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}