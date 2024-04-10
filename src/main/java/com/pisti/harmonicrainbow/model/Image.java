package com.pisti.harmonicrainbow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "images")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    private UUID id;
    @Column(nullable = false, length = 4096)
    private String image48Px;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime upload_time;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String format;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="user_id", insertable=false, updatable=false)
    private UUID userId;
}
