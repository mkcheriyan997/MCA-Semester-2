package com.lifeload.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LeaderboardEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;
    
    private double finalWealth;
    private int finalHappiness;
    
    private int survivalAge;
    
    // Balanced Score calculation: (wealth / 10000) + happiness + (relationships * 2) - stress
    private double balanceScore;
}
