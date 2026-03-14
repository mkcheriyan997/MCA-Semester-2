package com.lifeload.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EventOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    private String detailedDescription;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="healthDelta",       column=@Column(name="suc_health")),
        @AttributeOverride(name="happinessDelta",    column=@Column(name="suc_happiness")),
        @AttributeOverride(name="moneyDelta",        column=@Column(name="suc_money")),
        @AttributeOverride(name="knowledgeDelta",    column=@Column(name="suc_knowledge")),
        @AttributeOverride(name="energyDelta",       column=@Column(name="suc_energy")),
        @AttributeOverride(name="relationshipsDelta",column=@Column(name="suc_relationships")),
        @AttributeOverride(name="reputationDelta",   column=@Column(name="suc_reputation")),
        @AttributeOverride(name="stressDelta",       column=@Column(name="suc_stress")),
        @AttributeOverride(name="confidenceDelta",   column=@Column(name="suc_confidence")),
        @AttributeOverride(name="motivationDelta",   column=@Column(name="suc_motivation"))
    })
    private StatImpact impact;
    
    // Risk outcomes
    private Double riskProbability; // e.g. 0.3 for 30% chance of failure
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="healthDelta",       column=@Column(name="fail_health")),
        @AttributeOverride(name="happinessDelta",    column=@Column(name="fail_happiness")),
        @AttributeOverride(name="moneyDelta",        column=@Column(name="fail_money")),
        @AttributeOverride(name="knowledgeDelta",    column=@Column(name="fail_knowledge")),
        @AttributeOverride(name="energyDelta",       column=@Column(name="fail_energy")),
        @AttributeOverride(name="relationshipsDelta",column=@Column(name="fail_relationships")),
        @AttributeOverride(name="reputationDelta",   column=@Column(name="fail_reputation")),
        @AttributeOverride(name="stressDelta",       column=@Column(name="fail_stress")),
        @AttributeOverride(name="confidenceDelta",   column=@Column(name="fail_confidence")),
        @AttributeOverride(name="motivationDelta",   column=@Column(name="fail_motivation"))
    })
    private StatImpact failImpact;
}
