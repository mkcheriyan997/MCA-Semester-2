package com.lifeload.server.engine;

import com.lifeload.server.entity.Achievement;
import com.lifeload.server.entity.PlayerAchievement;
import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.PlayerStats;
import com.lifeload.server.repository.AchievementRepository;
import com.lifeload.server.repository.PlayerAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementManager {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private PlayerAchievementRepository playerAchievementRepository;

    public void checkAchievements(PlayerProfile profile) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<PlayerAchievement> unlocked = playerAchievementRepository.findByPlayerProfile(profile);
        
        PlayerStats stats = profile.getStats();

        for (Achievement ach : allAchievements) {
            boolean alreadyUnlocked = unlocked.stream().anyMatch(pa -> pa.getAchievement().getId().equals(ach.getId()));
            if (alreadyUnlocked) continue;

            boolean conditionMet = false;
            if ("MILLIONAIRE".equals(ach.getId()) && stats.getMoney() >= 1000000) conditionMet = true;
            if ("WORK_LIFE_BALANCE".equals(ach.getId()) && stats.getHappiness() > 80 && stats.getMoney() > 50000) conditionMet = true;
            if ("EXTREME_LONELINESS".equals(ach.getId()) && stats.getRelationships() == 0) conditionMet = true;

            if (conditionMet) {
                PlayerAchievement pa = new PlayerAchievement();
                pa.setPlayerProfile(profile);
                pa.setAchievement(ach);
                pa.setUnlockedAtAge(profile.getAge());
                playerAchievementRepository.save(pa);
                
                // Bonus for unlocking
                stats.setHappiness(Math.min(100, stats.getHappiness() + 10));
                stats.setConfidence(Math.min(100, stats.getConfidence() + 10));
            }
        }
    }
}
