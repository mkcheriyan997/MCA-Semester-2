package com.lifeload.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class NpcRival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private PlayerProfile playerProfile;

    private String name;
    private String type; // COWORKER, STARTUP_FOUNDER, INVESTOR
    private int level = 1;
    private double wealth = 500.0;
    private String currentGoal;
    private int ageStarted = 18;
}
