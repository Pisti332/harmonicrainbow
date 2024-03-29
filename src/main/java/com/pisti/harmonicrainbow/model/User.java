package com.pisti.harmonicrainbow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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
    private UUID user_id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean isActive;
    private UUID emailConfirmationToken;
    @Column(nullable = false)
    private LocalDateTime registryDate;
    @Column(nullable = false)
    private boolean isLoggedIn;
    @Column(nullable = false)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Image> images;
}
