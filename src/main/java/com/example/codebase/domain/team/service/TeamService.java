package com.example.codebase.domain.team.service;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.dto.TeamRequest;
import com.example.codebase.domain.team.dto.TeamResponse;
import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.entity.TeamUserRole;
import com.example.codebase.domain.team.repository.TeamRepository;
import com.example.codebase.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public TeamResponse.Get createTeam(TeamRequest.Create request, Member member) {
        if(teamRepository.existsByName(request.getName())) {
            throw new RuntimeException("이미 존재하는 팀 이름입니다.");
        }

        Team team = Team.toEntity(request);
        TeamUser teamUser = TeamUser.toEntity(request.getPosition(), member, team, TeamUserRole.OWNER);

        team.addTeamUser(teamUser);

        teamRepository.save(team);
        return TeamResponse.Get.from(team);
    }

    @Transactional(readOnly = true)
    public TeamResponse.Get getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("팀이 존재하지 않습니다."));
        return TeamResponse.Get.from(team);
    }

    public TeamResponse.Get updateTeam(TeamRequest.Update request, TeamUser teamUser) {
        Team team = teamUser.getTeam();

        if (teamRepository.existsByNameAndIdNot(request.getName(), team.getId())) {
            throw new RuntimeException("이미 존재하는 팀 이름입니다.");
        }

        team.update(request);
        teamRepository.save(team);
        return TeamResponse.Get.from(team);
    }

    public void deleteTeam(TeamUser teamUser) {
        Team team = teamUser.getTeam();
        teamRepository.delete(team);
    }
}
