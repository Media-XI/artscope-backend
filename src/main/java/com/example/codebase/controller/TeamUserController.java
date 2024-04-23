package com.example.codebase.controller;

import com.example.codebase.annotation.LoginOnly;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.team.dto.TeamUserRequest;
import com.example.codebase.domain.team.dto.TeamUserResponse;
import com.example.codebase.domain.team.entity.TeamUser;
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
@Tag(name = "팀 유저 API", description = "팀 유저와 권한에 관련된 API ")
@RequestMapping("/api/team-users")
public class TeamUserController {

    private final TeamUserService teamUserService;

    private final MemberService memberService;

    @Autowired
    public TeamUserController(TeamUserService teamUserService, MemberService memberService) {
        this.teamUserService = teamUserService;
        this.memberService = memberService;
    }

    @PostMapping("/{username}")
    @LoginOnly
    @Operation(summary = "팀 유저 추가", description = "팀에 유저를 추가합니다.")
    public ResponseEntity addTeamUser(@PathVariable String username, @RequestParam Long teamId, @RequestBody @Valid TeamUserRequest.Create request) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        TeamUser loginUser = teamUserService.findByTeamIdAndUsername(teamId, loginUsername);
        loginUser.validOwner();

        Member inviteUser = memberService.getEntity(username);

        teamUserService.addTeamUser(loginUser, inviteUser, request);
        TeamUserResponse.GetAll response = teamUserService.getTeamUsers(teamId);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    @LoginOnly
    @Operation(summary = "팀 유저 삭제(추방)", description = "팀에 속한 유저를 삭제합니다.")
    public ResponseEntity deleteTeamUser(@PathVariable String username, @RequestParam Long teamId) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        TeamUser loginUser = teamUserService.findByTeamIdAndUsername(teamId, loginUsername);

        TeamUser deleteUser = teamUserService.findByTeamIdAndUsername(teamId, username);

        teamUserService.deleteTeamUser(loginUser, deleteUser);
        TeamUserResponse.GetAll response = teamUserService.getTeamUsers(teamId);
        return new ResponseEntity(response, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{username}/owner")
    @LoginOnly
    @Operation(summary = "팀 권한 양도", description = "팀의 권한을 양도합니다.")
    public ResponseEntity transferToOwner(@PathVariable String username, @RequestParam Long teamId) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        TeamUser loginUser = teamUserService.findByTeamIdAndUsername(teamId, loginUsername);
        loginUser.validOwner();

        TeamUser transferUser = teamUserService.findByTeamIdAndUsername(teamId, username);

        teamUserService.transferToOwner(loginUser, transferUser);
        TeamUserResponse.GetAll response = teamUserService.getTeamUsers(teamId);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PatchMapping("/{username}")
    @LoginOnly
    @Operation(summary = "팀 유저 직책 변경", description = "팀 유저의 직책을 변경합니다.")
    public ResponseEntity updateTeamUser(@PathVariable String username, @RequestParam Long teamId, @RequestBody @Valid TeamUserRequest.Update request) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        TeamUser member = teamUserService.findByTeamIdAndUsername(teamId, loginUsername);

        TeamUser changeMember = teamUserService.findByTeamIdAndUsername(teamId, username);

        teamUserService.updateTeamUser(member, changeMember, request);

        return new ResponseEntity("직책이 변경되었습니다.", HttpStatus.OK);
    }

}
