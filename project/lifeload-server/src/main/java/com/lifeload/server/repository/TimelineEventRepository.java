package com.lifeload.server.repository;

import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimelineEventRepository extends JpaRepository<TimelineEvent, Long> {
    List<TimelineEvent> findByPlayerProfileOrderByAgeAscWeekAsc(PlayerProfile profile);
}
