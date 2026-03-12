package com.lifeload.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TimelineEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private PlayerProfile playerProfile;

    private int age;
    private int week;
    private String description;
    private String eventType; // CAREER, MONEY, HEALTH, RELATIONSHIP, ACHIEVEMENT, CRISIS
    private LocalDateTime recordedAt = LocalDateTime.now();
}
