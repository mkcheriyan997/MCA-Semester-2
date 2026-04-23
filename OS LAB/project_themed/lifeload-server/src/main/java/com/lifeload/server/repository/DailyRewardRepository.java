package com.lifeload.server.repository;

import com.lifeload.server.entity.DailyReward;
import com.lifeload.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DailyRewardRepository extends JpaRepository<DailyReward, Long> {
    Optional<DailyReward> findByUser(User user);
}
