package com.lifeload.server.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class StatImpact {
    private int healthDelta;
    private int happinessDelta;
    private double moneyDelta;
    private int knowledgeDelta;
    private int energyDelta;
    private int relationshipsDelta;
    private int reputationDelta;
    private int stressDelta;
    private int confidenceDelta;
    private int motivationDelta;
}
