package com.lifeload.server.repository;

import com.lifeload.server.entity.LeaderboardEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {
    
    @Query("SELECT l FROM LeaderboardEntry l ORDER BY l.finalWealth DESC")
    List<LeaderboardEntry> findTopByWealth(Pageable pageable);
    
    @Query("SELECT l FROM LeaderboardEntry l ORDER BY l.balanceScore DESC")
    List<LeaderboardEntry> findTopByBalanceScore(Pageable pageable);
}
