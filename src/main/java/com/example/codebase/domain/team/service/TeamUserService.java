package com.example.codebase.domain.team.service;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.dto.TeamResponse;
import com.example.codebase.domain.team.dto.TeamUserRequest;
import com.example.codebase.domain.team.dto.TeamUserResponse;
import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.entity.TeamUserRole;
import com.example.codebase.domain.team.repository.TeamRepository;
import com.example.codebase.domain.team.repository.TeamUserRepository;
import com.example.codebase.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TeamUserService {

    private final TeamUserRepository teamUserRepository;

    private final TeamRepository teamRepository;

    @Autowired
    public TeamUserService(TeamUserRepository teamUserRepository, TeamRepository teamRepository) {
        this.teamUserRepository = teamUserRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public TeamUser findByTeamIdAndUsername(Long teamId, String username) {
        if (!teamRepository.existsById(teamId)) {
            throw new NotFoundException("해당 팀이 존재하지 않습니다.");
        }

        return teamUserRepository.findByTeamIdAndUsername(teamId, username)
                .orElseThrow(() -> new NotFoundException("해당 팀에 속해있지 않습니다."));
    }

    @Transactional(readOnly = true)
    public TeamUserResponse.GetAll getTeamUsers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("해당 팀이 존재하지 않습니다."));

        return TeamUserResponse.GetAll.from(teamUserRepository.findAllByTeamOrderByRole(team));
    }

    public void addTeamUser(TeamUser member, Member inviteUser, TeamUserRequest.Create request) {
        Team team = member.getTeam();
        if (teamUserRepository.existsByTeamAndMember(team, inviteUser)) {
            throw new RuntimeException("이미 팀에 속한 멤버입니다.");
        }

        teamUserRepository.save(TeamUser.toEntity(request.getPosition(), inviteUser, team, TeamUserRole.MEMBER));
    }

    public void deleteTeamUser(TeamUser loginUser, TeamUser deleteUser) {
        if (loginUser.isOwner() && deleteUser.isOwner()) {
            throw new RuntimeException("팀장은 자신을 추방할 수 없습니다.");
        }
        teamUserRepository.delete(deleteUser);
    }

    public void transferToOwner(TeamUser loginUser, TeamUser transferUser) {
        loginUser.transferOwner(transferUser);

        teamUserRepository.save(loginUser);
        teamUserRepository.save(transferUser);
    }

    public void updateTeamUser(TeamUser member, TeamUser changeMember, TeamUserRequest.Update request) {
        if (!(Objects.equals(member.getId(), changeMember.getId())) && !member.isOwner()) {
            throw new RuntimeException("본인 또는 팀장만 정보를 수정할 수 있습니다.");
        }

        member.update(request);
        teamUserRepository.save(member);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse.ProfileGet> getTeamsByUsername(Member member) {
        List<TeamUser> teamUsers = teamUserRepository.findByMemberOrderByCreatedTimeAsc(member);

        List<TeamResponse.ProfileGet> response = new ArrayList<>();
        for (TeamUser teamUser : teamUsers) {
            TeamResponse.ProfileGet profileGet = TeamResponse.ProfileGet.from(teamUser);
            response.add(profileGet);
        }

        return response;
    }
}
