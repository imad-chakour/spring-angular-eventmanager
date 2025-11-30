package com.example.event_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reg_seq")
    @SequenceGenerator(name = "reg_seq", sequenceName = "REGISTRATIONS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RegistrationStatus status;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "confirmation_date")
    private LocalDateTime confirmationDate;

    @Column(name = "participation_confirmed")
    private Boolean participationConfirmed = false;

    public Registration() {
        this.registrationDate = LocalDateTime.now();
        this.status = RegistrationStatus.PENDING;
    }
}

