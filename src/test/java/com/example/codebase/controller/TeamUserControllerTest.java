package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.team.dto.TeamRequest;
import com.example.codebase.domain.team.dto.TeamUserRequest;
import com.example.codebase.domain.team.dto.TeamUserResponse;
import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.service.TeamService;
import com.example.codebase.domain.team.service.TeamUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class TeamUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamUserService teamUserService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Member createMember(String username) {
        CreateMemberDTO createMemberDTO = new CreateMemberDTO();
        createMemberDTO.setUsername(username);
        createMemberDTO.setPassword("password");
        createMemberDTO.setName("name");
        createMemberDTO.setEmail("email" + "@" + username + ".com");
        createMemberDTO.setAllowEmailReceive(true);

        memberService.createMember(createMemberDTO);
        return memberService.getEntity(username);
    }

    public TeamRequest.Create createTeamRequest(String name) {
        return new TeamRequest.Create(
                name,
                "팀 주소",
                "http://test.com/profile.jpg",
                "http://test.com/background.jpg",
                "팀소개",
                "자신의 포지션, 직급"
        );
    }

    public Team createTeam(Member member, String name) {
        return teamService.createTeam(createTeamRequest(name), member);
    }

    public void createAndInviteMember(TeamUser loginUser, Member inviteMember) {
        TeamUserRequest.Create request = new TeamUserRequest.Create(
                "팀원"
        );
        teamUserService.addTeamUser(loginUser, inviteMember, request);
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 소속 유저 추가")
    @Test
    void 팀_소속_유저_추가() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");
        Member inviteMember = createMember("inviteMember");

        TeamUserRequest.Create request = new TeamUserRequest.Create(
                "팀원"
        );

        // when
        String response = mockMvc.perform(
                        post("/api/team-users/" + inviteMember.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        TeamUserResponse.GetAll teamUserResponse = objectMapper.readValue(response, TeamUserResponse.GetAll.class);
        assertEquals(2, teamUserResponse.getTeamUsers().size());
        assertEquals("testid", teamUserResponse.getTeamUsers().get(0).getUsername());
        assertEquals("inviteMember", teamUserResponse.getTeamUsers().get(1).getUsername());
        assertEquals("팀원", teamUserResponse.getTeamUsers().get(1).getPosition());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 소속 유저 삭제(추방)")
    @Test
    void 팀_소속_유저_삭제() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(team.getId(), member.getUsername());

        Member inviteMember = createMember("removedUser");
        createAndInviteMember(teamOwner, inviteMember);

        // when
        String response = mockMvc.perform(
                        delete("/api/team-users/" + inviteMember.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        TeamUserResponse.GetAll teamUserResponse = objectMapper.readValue(response, TeamUserResponse.GetAll.class);
        assertEquals(1, teamUserResponse.getTeamUsers().size());
        assertEquals("testid", teamUserResponse.getTeamUsers().get(0).getUsername());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀장인 경우 자신을 팀에서 추방시 실패")
    @Test
    void 팀장인_경우_자신을_팀에서_추방_실패() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");

        // when
        String response = mockMvc.perform(
                        delete("/api/team-users/" + member.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(response.contains("팀장은 자신을 추방할 수 없습니다."));  // 기억할것 contains
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀장 권한 양도")
    @Test
    void 팀장_권한_양도() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(team.getId(), member.getUsername());

        Member inviteMember = createMember("transferUser");
        createAndInviteMember(teamOwner, inviteMember);

        // when
        String response = mockMvc.perform(
                       patch("/api/team-users/" + inviteMember.getUsername() + "/owner?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        TeamUserResponse.GetAll teamUserResponse = objectMapper.readValue(response, TeamUserResponse.GetAll.class);
        assertEquals(2, teamUserResponse.getTeamUsers().size());
        assertEquals("transferUser", teamUserResponse.getTeamUsers().get(0).getUsername());
        assertEquals("OWNER", teamUserResponse.getTeamUsers().get(0).getRole().name());
        assertEquals("testid", teamUserResponse.getTeamUsers().get(1).getUsername());
        assertEquals("MEMBER", teamUserResponse.getTeamUsers().get(1).getRole().name());
    }

    @WithMockCustomUser(username = "leaveUser", role = "USER")
    @DisplayName("팀 탈퇴 성공")
    @Test
    void 팀_탈퇴_성공() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(team.getId(), member.getUsername());

        Member inviteMember = createMember("leaveUser");
        createAndInviteMember(teamOwner, inviteMember);

        // when
        String response = mockMvc.perform(
                        delete("/api/team-users/" + inviteMember.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        TeamUserResponse.GetAll teamUserResponse = objectMapper.readValue(response, TeamUserResponse.GetAll.class);
        assertEquals(1, teamUserResponse.getTeamUsers().size());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀장이 팀 유저 직책 변경")
    @Test
    void 팀장이_팀_유저_직책_변경() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(team.getId(), member.getUsername());

        Member inviteMember = createMember("changePositionUser");
        createAndInviteMember(teamOwner, inviteMember); // 팀원

        TeamUserRequest.Update request = new TeamUserRequest.Update(
                "변경된포지션"
        );

        // when
        String response = mockMvc.perform(
                        patch("/api/team-users/" + inviteMember.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(response.contains("직책이 변경되었습니다."));

    }

    @WithMockCustomUser(username = "changePositionUser", role = "USER")
    @DisplayName("팀원이 본인 직책 변경")
    @Test
    void 팀원이_본인_직책_변경() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(team.getId(), member.getUsername());

        Member inviteMember = createMember("changePositionUser");
        createAndInviteMember(teamOwner, inviteMember); // 팀원

        TeamUserRequest.Update request = new TeamUserRequest.Update(
                "변경된포지션"
        );

        // when
        String response = mockMvc.perform(
                        patch("/api/team-users/" + inviteMember.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(response.contains("직책이 변경되었습니다."));
    }

    @WithMockCustomUser(username = "otherUser", role = "USER")
    @DisplayName("다른 유저가 팀원 직책 변경시도시 실패")
    @Test
    void 다른_유저가_팀원_직책_변경시도시_실패() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(team.getId(), member.getUsername());

        Member teamMember1 = createMember("user1");
        createAndInviteMember(teamOwner, teamMember1); // 팀원

        Member teamMember2 = createMember("otherUser");
        createAndInviteMember(teamOwner, teamMember2); // 팀원

        TeamUserRequest.Update request = new TeamUserRequest.Update(
                "변경된포지션"
        );

        // when
        String response = mockMvc.perform(
                        patch("/api/team-users/" + teamMember1.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(response.contains("본인 또는 팀장만 정보를 수정할 수 있습니다."));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 소속 유저 추가시 이미 팀에 속한 멤버인 경우 실패")
    @Test
    void 팀_소속_유저_추가시_이미_팀에_속했을경우_실패() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(team.getId(), member.getUsername());

        Member inviteMember = createMember("addMember");
        createAndInviteMember(teamOwner, inviteMember);

        TeamUserRequest.Create request = new TeamUserRequest.Create(
                "팀원"
        );

        // when
        String response = mockMvc.perform(
                        post("/api/team-users/" + inviteMember.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(response.contains("이미 팀에 속한 멤버입니다."));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀장이 자기 자신을 추방 할 경우 실패")
    @Test
    void 팀장이_자기_자신을_추방_할_경우_실패() throws Exception {
        // given
        Member member = createMember("testid");
        Team team = createTeam(member, "팀이름");

        String response = mockMvc.perform(
                        delete("/api/team-users/" + member.getUsername() + "?teamId=" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        //then
        assertTrue(response.contains("팀장은 자신을 추방할 수 없습니다."));
    }
}
