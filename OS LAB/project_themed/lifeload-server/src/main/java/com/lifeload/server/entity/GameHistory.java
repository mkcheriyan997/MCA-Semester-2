package com.lifeload.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private PlayerProfile playerProfile;

    private int age;
    private int weekNumber; // 1-52
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private LocalDateTime timestamp;
}
