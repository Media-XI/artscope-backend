package com.example.codebase.domain.team.repository;

import com.example.codebase.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

}
