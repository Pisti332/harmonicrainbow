package com.harmonicrainbow.userservice.model;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    public User(String email, String password, boolean isActive, UUID emailConfirmationToken) {
        this.email = email;
        this.password = String.valueOf(password.hashCode());
        this.isActive = isActive;
        this.emailConfirmationToken = emailConfirmationToken;
        this.registryDate = LocalDateTime.now();
    }

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = false, nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean isActive;
    private UUID emailConfirmationToken;
    @Column(nullable = false)
    private LocalDateTime registryDate;

}
