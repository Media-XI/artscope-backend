package com.example.codebase.controller;

import com.example.codebase.annotation.LoginOnly;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.team.dto.TeamRequest;
import com.example.codebase.domain.team.dto.TeamResponse;
import com.example.codebase.domain.team.dto.TeamUserResponse;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.service.TeamService;
import com.example.codebase.domain.team.service.TeamUserService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "팀 API", description = "팀과 관련된 API")
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    private final MemberService memberService;

    private final TeamUserService teamUserService;

    @Autowired
    public TeamController(TeamService teamService, MemberService memberService, TeamUserService teamUserService) {
        this.teamService = teamService;
        this.memberService = memberService;
        this.teamUserService = teamUserService;
    }

    @PostMapping
    @Operation(summary = "팀 생성", description = "팀을 생성합니다.")
    @LoginOnly
    public ResponseEntity createTeam(@RequestBody @Valid TeamRequest.Create request) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        Member member = memberService.getEntity(loginUsername);

        TeamResponse.Get response = teamService.createTeam(request, member);

        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀 정보 조회", description = "팀을 조회합니다.")
    public ResponseEntity getTeam(@PathVariable Long teamId) {
        TeamResponse.Get response = teamService.getTeam(teamId);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping("/{teamId}")
    @Operation(summary = "팀 정보 수정", description = "팀 정보를 수정합니다.")
    @LoginOnly
    public ResponseEntity updateTeam(@PathVariable Long teamId, @RequestBody @Valid TeamRequest.Update request) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        TeamUser member = teamUserService.findByTeamIdAndUsername(teamId, loginUsername);
        member.validOwner();

        TeamResponse.Get response = teamService.updateTeam(request, member);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "팀 삭제", description = "팀을 삭제합니다.")
    @LoginOnly
    public ResponseEntity deleteTeam(@PathVariable Long teamId) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        TeamUser member = teamUserService.findByTeamIdAndUsername(teamId, loginUsername);
        member.validOwner();

        teamService.deleteTeam(member);

        return new ResponseEntity("팀이 삭제되었습니다.", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{teamId}/people")
    @Operation(summary = "팀 소속 유저 조회", description = "팀에 속한 유저를 모두 조회 합니다.")
    public ResponseEntity getTeamUsers(@PathVariable Long teamId) {
        TeamUserResponse.GetAll response = teamUserService.getTeamUsers(teamId);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
