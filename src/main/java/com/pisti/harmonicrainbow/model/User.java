package com.pisti.harmonicrainbow.model;

import com.pisti.harmonicrainbow.security.Role;
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
public class User implements UserDetails {
    public User(String email, String password, boolean isActive, UUID emailConfirmationToken, Role role) {
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.emailConfirmationToken = emailConfirmationToken;
        this.registryDate = LocalDateTime.now();
        this.role = role;
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
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Image> images;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
