package com.lifeload.server.repository;

import com.lifeload.server.entity.Trait;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraitRepository extends JpaRepository<Trait, String> {
}
