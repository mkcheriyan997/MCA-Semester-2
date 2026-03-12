package com.lifeload.server.repository;

import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.PlayerAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerAchievementRepository extends JpaRepository<PlayerAchievement, Long> {
    List<PlayerAchievement> findByPlayerProfile(PlayerProfile profile);
}
