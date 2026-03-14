package com.lifeload.server.repository;

import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, Long> {
    Optional<PlayerProfile> findByUser(User user);
}
