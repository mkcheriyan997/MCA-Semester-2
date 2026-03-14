package com.lifeload.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Trait {
    @Id
    private String id; // e.g. "GENIUS", "SOCIAL"
    
    private String name;
    private String description;
}
