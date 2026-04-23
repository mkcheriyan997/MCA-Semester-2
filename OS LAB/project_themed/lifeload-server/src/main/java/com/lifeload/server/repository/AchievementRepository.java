package com.lifeload.server.repository;

import com.lifeload.server.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, String> {
}
