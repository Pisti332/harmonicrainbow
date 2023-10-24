package com.pisti.harmonicrainbow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    public User(String email, String password, boolean isActive, UUID emailConfirmationToken) {
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.emailConfirmationToken = emailConfirmationToken;
        this.registryDate = LocalDateTime.now();
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
    @Column(nullable = false)
    private boolean isLoggedIn;

}
