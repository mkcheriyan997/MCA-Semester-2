package com.lifeload.server.repository;

import com.lifeload.server.entity.NpcRival;
import com.lifeload.server.entity.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NpcRivalRepository extends JpaRepository<NpcRival, Long> {
    List<NpcRival> findByPlayerProfile(PlayerProfile profile);
}
