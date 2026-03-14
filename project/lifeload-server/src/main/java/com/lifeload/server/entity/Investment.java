package com.lifeload.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private PlayerProfile playerProfile;

    private String type; // STOCK, STARTUP, REAL_ESTATE
    private String name; // e.g. "TechCorp Stock"
    
    private double initialAmount;
    private double currentAmount;
    
    private int investedAtAge;
    private int investedAtWeek;
}
