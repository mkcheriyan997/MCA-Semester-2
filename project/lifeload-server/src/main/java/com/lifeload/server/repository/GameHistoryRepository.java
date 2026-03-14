package com.lifeload.server.repository;

import com.lifeload.server.entity.GameHistory;
import com.lifeload.server.entity.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    List<GameHistory> findByPlayerProfileOrderByTimestampDesc(PlayerProfile profile);
}
