package com.example.codebase.domain.team.repository;

import com.example.codebase.domain.team.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {
}
