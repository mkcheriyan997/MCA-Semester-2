package com.lifeload.server.engine;

import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.PlayerSkill;
import com.lifeload.server.entity.Skill;
import com.lifeload.server.repository.PlayerSkillRepository;
import com.lifeload.server.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillManager {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private PlayerSkillRepository playerSkillRepository;

    public void gainExperience(PlayerProfile profile, String skillId, int amount) {
        Optional<Skill> skillOpt = skillRepository.findById(skillId);
        if (skillOpt.isEmpty()) return;

        Skill skill = skillOpt.get();
        PlayerSkill playerSkill = playerSkillRepository.findByPlayerProfile(profile)
            .stream()
            .filter(ps -> ps.getSkill().getId().equals(skillId))
            .findFirst()
            .orElseGet(() -> {
                PlayerSkill newPs = new PlayerSkill();
                newPs.setPlayerProfile(profile);
                newPs.setSkill(skill);
                newPs.setLevel(1);
                newPs.setExperience(0);
                return newPs;
            });

        playerSkill.setExperience(playerSkill.getExperience() + amount);
        
        // Level up mechanic: requires 100 * level exp
        int rxp = playerSkill.getLevel() * 100;
        if (playerSkill.getExperience() >= rxp) {
            playerSkill.setLevel(playerSkill.getLevel() + 1);
            playerSkill.setExperience(playerSkill.getExperience() - rxp);
            
            // Give stat bonuses on level up depending on category
            if ("Health".equals(skill.getCategory())) {
                profile.getStats().setHealth(Math.min(100, profile.getStats().getHealth() + 5));
            } else if ("Business".equals(skill.getCategory())) {
                profile.getStats().setMotivation(Math.min(100, profile.getStats().getMotivation() + 5));
            } else if ("Education".equals(skill.getCategory())) {
                profile.getStats().setKnowledge(profile.getStats().getKnowledge() + 20);
            }
        }
        
        playerSkillRepository.save(playerSkill);
    }
}
