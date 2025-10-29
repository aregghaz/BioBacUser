package com.biobac.users.repository;

import com.biobac.users.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PositionRepository extends JpaRepository<Position, Long>, JpaSpecificationExecutor<Position> {
    Position findByName(String name);
}
