package com.lifeload.server.repository;

import com.lifeload.server.entity.GameEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface GameEventRepository extends JpaRepository<GameEvent, Long> {
    @Query("SELECT e FROM GameEvent e WHERE " +
           "(e.minAge IS NULL OR e.minAge <= :age) AND " +
           "(e.maxAge IS NULL OR e.maxAge >= :age) AND " +
           "(e.minStress IS NULL OR e.minStress <= :stress) AND " +
           "(e.maxMoney IS NULL OR e.maxMoney >= :money)")
    List<GameEvent> findValidEvents(int age, int stress, double money);
}
