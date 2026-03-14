package com.lifeload.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PlayerStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "stats")
    @JsonIgnore
    private PlayerProfile playerProfile;

    // Physical & Mental State
    private int health = 100;
    private int happiness = 100;
    private int energy = 100;
    private int stress = 0;
    
    // Abstract Stats
    private double money = 500.0;
    private int knowledge = 0;
    private int relationships = 50;
    private int reputation = 50;

    // Emotional Stats
    private int confidence = 50;
    private int motivation = 50;
}
