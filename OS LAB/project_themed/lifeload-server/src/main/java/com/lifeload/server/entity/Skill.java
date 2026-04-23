package com.lifeload.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Skill {
    @Id
    private String id; // e.g., "PROGRAMMING", "FITNESS"
    
    private String name;
    private String category; // e.g., "Education", "Business", "Health"
    private String description;
}
