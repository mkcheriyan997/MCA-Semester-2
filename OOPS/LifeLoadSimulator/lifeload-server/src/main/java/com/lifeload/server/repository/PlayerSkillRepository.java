package com.lifeload.server.repository;

import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.PlayerSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerSkillRepository extends JpaRepository<PlayerSkill, Long> {
    List<PlayerSkill> findByPlayerProfile(PlayerProfile profile);
}
