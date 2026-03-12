package com.lifeload.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class PlayerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String playerName;

    private int age = 18; // Default starting age
    private int currentWeek = 1; // 1 to 52 weeks in a year
    private String status = "ACTIVE"; // ACTIVE, WON, FAILED
    private String endReason;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "stats_id")
    private PlayerStats stats;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_traits",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "trait_id")
    )
    private List<Trait> traits = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String pendingEventJson; // Stores JSON of an event waiting for a player choice
}
