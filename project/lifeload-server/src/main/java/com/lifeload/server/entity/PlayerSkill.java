package com.lifeload.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "player_skills")
@Getter
@Setter
public class PlayerSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private PlayerProfile playerProfile;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private int level = 1;
    private int experience = 0;
}
