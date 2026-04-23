package com.lifeload.server.repository;

import com.lifeload.server.entity.Investment;
import com.lifeload.server.entity.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByPlayerProfile(PlayerProfile profile);
}
