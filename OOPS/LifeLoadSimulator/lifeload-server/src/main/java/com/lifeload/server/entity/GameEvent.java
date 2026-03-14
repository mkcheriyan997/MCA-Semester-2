package com.lifeload.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class GameEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Conditions to trigger (e.g. minAge, maxAge, minStress, maxMoney)
    private Integer minAge;
    private Integer maxAge;
    private Integer minStress;
    private Double maxMoney;
    
    // Impact on stats if it's an outcome-only event (no choices)
    @Embedded
    private StatImpact defaultImpact;

    // If the event has choices
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private List<EventOption> options;
}
