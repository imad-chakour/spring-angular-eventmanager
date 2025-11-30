package com.example.participant_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "part_seq")
    @SequenceGenerator(name = "part_seq", sequenceName = "PARTICIPANTS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "company")
    private String company;

    @Column(name = "job_title")
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParticipantStatus status;

    @ElementCollection
    @CollectionTable(name = "participant_segments", joinColumns = @JoinColumn(name = "participant_id"))
    @Column(name = "segment_name")
    private List<String> segments = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "participant_preferences", joinColumns = @JoinColumn(name = "participant_id"))
    @Column(name = "preference_value")
    private List<String> communicationPreferences = new ArrayList<>();

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "opt_in_marketing")
    private Boolean optInMarketing = true;

    public Participant() {
        this.registrationDate = LocalDateTime.now();
        this.status = ParticipantStatus.ACTIVE;
    }

    public Participant(long l, String mail, String fallback, String participant, String s, String fallbackCorp, String participant1, ParticipantStatus participantStatus, List<String> strings, List<String> email, LocalDateTime now, LocalDateTime now1, boolean b) {
    }
}