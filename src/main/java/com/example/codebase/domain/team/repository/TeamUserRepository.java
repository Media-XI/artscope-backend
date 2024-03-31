package com.example.codebase.domain.team.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    @Query("SELECT tu FROM TeamUser tu LEFT JOIN Member m ON tu.member.username = m.username WHERE tu.team.id = :teamId AND m.username = :username")
    Optional<TeamUser> findByTeamIdAndUsername(Long teamId, String username);

    @Query("SELECT tu FROM TeamUser tu WHERE tu.team = :team ORDER BY CASE WHEN tu.role = 'OWNER' THEN 0 ELSE 1 END, tu.createdTime ASC")
    List<TeamUser> findAllByTeamOrderByRole(Team team);

    boolean existsByTeamAndMember(Team team, Member member);
}
