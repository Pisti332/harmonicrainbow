package com.harmonicrainbow.imageservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @Column(nullable = false)
    private LocalDateTime creationTime;
    @Column(nullable = false)
    private UUID token;

}
