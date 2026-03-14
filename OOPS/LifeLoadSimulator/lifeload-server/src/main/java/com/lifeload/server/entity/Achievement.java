package com.lifeload.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Achievement {
    @Id
    private String id; // e.g. "MILLIONAIRE"
    
    private String title;
    private String description;
    private String conditionScript; // e.g. "stats.money >= 1000000"
}
