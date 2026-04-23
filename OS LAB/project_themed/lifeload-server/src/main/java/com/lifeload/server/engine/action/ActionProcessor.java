package com.lifeload.server.engine.action;

import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.PlayerStats;
import com.lifeload.server.entity.Trait;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionProcessor {

    public String processAction(String actionId, PlayerProfile profile) {
        PlayerStats stats = profile.getStats();
        List<Trait> traits = profile.getTraits();

        // Trait modifiers (applied as multipliers)
        double knowledgeBonus  = hasTrait(traits, "GENIUS")    ? 1.6 : 1.0;
        double socialBonus     = hasTrait(traits, "SOCIAL")    ? 1.5 : 1.0;
        double careerBonus     = hasTrait(traits, "AMBITIOUS") ? 1.5 : 1.0;
        double stressResist    = hasTrait(traits, "CALM")      ? 0.5 : 1.0;
        double creativeBonus   = hasTrait(traits, "CREATIVE")  ? 1.3 : 1.0;

        switch (actionId.toLowerCase()) {
            case "work":
                if (stats.getEnergy() >= 20) {
                    double earnings = (200 + stats.getKnowledge() * 1.5) * careerBonus;
                    stats.setMoney(stats.getMoney() + earnings);
                    stats.setEnergy(stats.getEnergy() - 20);
                    stats.setStress(stats.getStress() + (int) (15 * stressResist));
                    stats.setMotivation(Math.max(0, stats.getMotivation() - 5));
                    stats.setReputation(Math.min(100, stats.getReputation() + 1));
                } else {
                    return "INSUFFICIENT_ENERGY";
                }
                break;

            case "study":
                if (stats.getMoney() < 50) {
                    stats.setStress(stats.getStress() + 25);
                    return "INSUFFICIENT_FUNDS";
                }
                if (stats.getEnergy() >= 15) {
                    stats.setMoney(stats.getMoney() - 50);
                    stats.setEnergy(stats.getEnergy() - 15);
                    stats.setKnowledge((int)(stats.getKnowledge() + 10 * knowledgeBonus));
                    stats.setStress(stats.getStress() + (int)(5 * stressResist));
                    stats.setMotivation(Math.min(100, stats.getMotivation() + 3));
                } else {
                    return "INSUFFICIENT_ENERGY";
                }
                break;

            case "rest":
                stats.setEnergy(Math.min(100, stats.getEnergy() + 40));
                stats.setStress(Math.max(0, stats.getStress() - (int)(20 * (2.0 - stressResist))));
                stats.setHealth(Math.min(100, stats.getHealth() + 5));
                stats.setHappiness(Math.min(100, stats.getHappiness() + 5));
                break;

            case "gym":
                if (stats.getMoney() < 20) {
                    stats.setStress(stats.getStress() + 25);
                    return "INSUFFICIENT_FUNDS";
                }
                if (stats.getEnergy() >= 10) {
                    stats.setMoney(stats.getMoney() - 20);
                    stats.setEnergy(stats.getEnergy() - 10);
                    stats.setHealth(Math.min(100, stats.getHealth() + 15));
                    stats.setStress(Math.max(0, stats.getStress() - (int)(15 * (2.0 - stressResist))));
                    stats.setConfidence(Math.min(100, stats.getConfidence() + 5));
                    stats.setMotivation(Math.min(100, stats.getMotivation() + 3));
                } else {
                    return "INSUFFICIENT_ENERGY";
                }
                break;

            case "socialize":
                if (stats.getMoney() < 100) {
                    stats.setStress(stats.getStress() + 25);
                    return "INSUFFICIENT_FUNDS";
                }
                if (stats.getEnergy() >= 15) {
                    stats.setMoney(stats.getMoney() - 100);
                    stats.setEnergy(stats.getEnergy() - 15);
                    stats.setRelationships((int) Math.min(100, stats.getRelationships() + 10 * socialBonus));
                    stats.setHappiness((int) Math.min(100, stats.getHappiness() + 20 * socialBonus));
                    stats.setStress(Math.max(0, stats.getStress() - (int)(10 * (2.0 - stressResist))));
                    stats.setReputation(Math.min(100, stats.getReputation() + 2));
                } else {
                    return "INSUFFICIENT_ENERGY";
                }
                break;

            case "meditate":
                stats.setStress(Math.max(0, stats.getStress() - (int)(30 * (2.0 - stressResist))));
                stats.setHappiness(Math.min(100, stats.getHappiness() + 10));
                stats.setEnergy(Math.min(100, stats.getEnergy() + 15));
                stats.setMotivation(Math.min(100, stats.getMotivation() + 10));
                break;

            case "network":
                if (stats.getMoney() < 150) {
                    stats.setStress(stats.getStress() + 25);
                    return "INSUFFICIENT_FUNDS";
                }
                stats.setMoney(stats.getMoney() - 150);
                stats.setReputation((int) Math.min(100, stats.getReputation() + 8 * creativeBonus));
                stats.setRelationships(Math.min(100, stats.getRelationships() + 5));
                stats.setMotivation(Math.min(100, stats.getMotivation() + 5));
                break;

            case "freelance":
                if (stats.getKnowledge() < 20) {
                    return "INSUFFICIENT_KNOWLEDGE"; // Need knowledge first
                }
                if (stats.getEnergy() >= 25) {
                    double pay = (150 + stats.getKnowledge() * 2) * creativeBonus * careerBonus;
                    stats.setMoney(stats.getMoney() + pay);
                    stats.setEnergy(stats.getEnergy() - 25);
                    stats.setStress(stats.getStress() + (int) (10 * stressResist));
                    stats.setKnowledge(stats.getKnowledge() + 2);
                } else {
                    return "INSUFFICIENT_ENERGY";
                }
                break;

            case "odd_jobs":
                if (stats.getEnergy() >= 20) {
                    stats.setMoney(stats.getMoney() + 250);
                    stats.setEnergy(stats.getEnergy() - 20);
                    stats.setStress(stats.getStress() + (int)(10 * stressResist));
                } else {
                    return "INSUFFICIENT_ENERGY";
                }
                break;

            case "hustle":
                if (stats.getEnergy() >= 40) {
                    stats.setMoney(stats.getMoney() + 450);
                    stats.setEnergy(stats.getEnergy() - 40);
                    stats.setStress(stats.getStress() + (int)(30 * stressResist));
                } else {
                    return "INSUFFICIENT_ENERGY";
                }
                break;

            default:
                return "UNKNOWN_ACTION";
        }

        // Clamp all stats
        stats.setHealth(Math.max(0, Math.min(100, stats.getHealth())));
        stats.setEnergy(Math.max(0, Math.min(100, stats.getEnergy())));
        stats.setHappiness(Math.max(0, Math.min(100, stats.getHappiness())));
        stats.setStress(Math.max(0, Math.min(100, stats.getStress())));
        stats.setRelationships(Math.max(0, Math.min(100, stats.getRelationships())));
        stats.setReputation(Math.max(0, Math.min(100, stats.getReputation())));
        stats.setConfidence(Math.max(0, Math.min(100, stats.getConfidence())));
        stats.setMotivation(Math.max(0, Math.min(100, stats.getMotivation())));

        return "SUCCESS";
    }

    private boolean hasTrait(List<Trait> traits, String traitId) {
        return traits != null && traits.stream().anyMatch(t -> traitId.equalsIgnoreCase(t.getId()));
    }
}
