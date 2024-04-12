package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.team.dto.TeamRequest;
import com.example.codebase.domain.team.dto.TeamResponse;
import com.example.codebase.domain.team.dto.TeamUserRequest;
import com.example.codebase.domain.team.dto.TeamUserResponse;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.service.TeamService;
import com.example.codebase.domain.team.service.TeamUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class TeamControllerTest {

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
        TeamRequest.Create request = new TeamRequest.Create(
                name,
                "팀 주소",
                "http://test.com/profile.jpg",
                "http://test.com/background.jpg",
                "팀소개",
                "자신의 포지션, 직급"
        );
        return request;
    }

    public TeamResponse.Get createTeam(Member member, String name) {
        return teamService.createTeam(createTeamRequest(name), member);
    }

    public void createAndInviteMember(TeamUser loginUser, Member inviteMember) {
        TeamUserRequest.Create request = new TeamUserRequest.Create(
                "팀원"
        );
        teamUserService.addTeamUser(loginUser, inviteMember, request);
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 생성 테스트")
    @Test
    void 팀_생성() throws Exception {
        // given
        Member member = createMember("testid");
        TeamRequest.Create request = createTeamRequest("팀생성");

        // when
        String response = mockMvc.perform(
                        post("/api/teams")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        TeamResponse.Get teamResponse = objectMapper.readValue(response, TeamResponse.Get.class);
        assertEquals(request.getName(), teamResponse.getName());
        assertEquals(request.getAddress(), teamResponse.getAddress());
        assertEquals(request.getProfileImage(), teamResponse.getProfileImage());
        assertEquals(request.getBackgroundImage(), teamResponse.getBackgroundImage());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("이미 존재하는 팀 이름이 있을 때 실패")
    @Test
    void 이미_존재하는_팀_이름이_있을떄_실패() throws Exception {
        // given
        Member member = createMember("testid");
        createTeam(member, "동일한이름");
        TeamRequest.Create request = createTeamRequest("동일한이름");

        // when
        mockMvc.perform(
                        post("/api/teams")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("이미 존재하는 팀 이름입니다.", result.getResolvedException().getMessage()));
    }

    @DisplayName("팀 정보 상세 조회")
    @Test
    void 팀_정보_상세_조회() throws Exception {
        // given
        Member member = createMember("testid");
        TeamResponse.Get team = createTeam(member, "팀생성");

        String response = mockMvc.perform(
                        get("/api/teams/" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        TeamResponse.Get teamResponse = objectMapper.readValue(response, TeamResponse.Get.class);
        assertEquals(team.getName(), teamResponse.getName());
        assertEquals(team.getAddress(), teamResponse.getAddress());
        assertEquals(team.getProfileImage(), teamResponse.getProfileImage());
        assertEquals(team.getBackgroundImage(), teamResponse.getBackgroundImage());
    }

    @DisplayName("팀 정보 수정")
    @WithMockCustomUser(username = "testid", role = "USER")
    @Test
    void 팀_정보_수정() throws Exception {
        // given
        Member member = createMember("testid");
        TeamResponse.Get team = createTeam(member, "팀수정");

        TeamRequest.Update request = new TeamRequest.Update(
                "수정된팀이름",
                "수정된팀주소",
                "http://test.com/profile.jpg",
                "http://test.com/background.jpg",
                "수정된팀소개"
        );

        // when
        String response = mockMvc.perform(
                        put("/api/teams/" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        TeamResponse.Get teamResponse = objectMapper.readValue(response, TeamResponse.Get.class);
        assertEquals(request.getName(), teamResponse.getName());
        assertEquals(request.getAddress(), teamResponse.getAddress());
        assertEquals(request.getProfileImage(), teamResponse.getProfileImage());
        assertEquals(request.getBackgroundImage(), teamResponse.getBackgroundImage());
        assertEquals(request.getDescription(), teamResponse.getDescription());
    }

    @DisplayName("이미 존재하는 팀 이름이 있을 때 팀 정보 수정 실패")
    @WithMockCustomUser(username = "testid", role = "USER")
    @Test
    void 이미_존재하는_팀_이름이_있을때_팀_정보_수정_실패() throws Exception {
        // given
        Member member = createMember("testid");
        createTeam(member, "중복된팀이름");
        TeamResponse.Get team = createTeam(member, "테스트이름");
        TeamRequest.Update request = new TeamRequest.Update(
                "중복된팀이름",
                "팀 주소",
                "http://test.com/profile.jpg",
                "http://test.com/background.jpg",
                "팀소개"
        );

        // when
        mockMvc.perform(
                        put("/api/teams/" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("이미 존재하는 팀 이름입니다.", result.getResolvedException().getMessage()));
    }

    @DisplayName("기존의 팀과 동일한 이름으로 팀 정보 수정시")
    @WithMockCustomUser(username = "testid", role = "USER")
    @Test
    void 기존의_팀과_동일한_이름으로_팀_정보_수정시() throws Exception {
        // given
        Member member = createMember("testid");
        TeamResponse.Get team = createTeam(member, "팀이름");
        TeamRequest.Update request = new TeamRequest.Update(
                "팀이름",
                "팀 주소",
                "http://test.com/profile.jpg",
                "http://test.com/background.jpg",
                "팀소개"
        );

        // when
        mockMvc.perform(
                        put("/api/teams/" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("팀 삭제")
    @WithMockCustomUser(username = "testid", role = "USER")
    @Test
    void 팀_삭제() throws Exception {
        // given
        Member member = createMember("testid");
        TeamResponse.Get team = createTeam(member, "팀이름");

        // when
        mockMvc.perform(
                        delete("/api/teams/" + team.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 소속 유저 조회")
    @Test
    void 팀_소속_유저_조회() throws Exception {
        // given
        Member member = createMember("testid");
        TeamResponse.Get team = createTeam(member, "팀이름");

        // when
        String response = mockMvc.perform(get("/api/teams/" + team.getId() + "/people")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        TeamUserResponse.GetAll teamUserResponse = objectMapper.readValue(response, TeamUserResponse.GetAll.class);
        assertEquals(1, teamUserResponse.getTeamUsers().size());
        assertEquals("testid", teamUserResponse.getTeamUsers().get(0).getUsername());
    }

    @DisplayName("해당 유저의 팀 목록 조회 ")
    @Test
    void 유저가_속한_팀_조회() throws Exception {
        //given
        Member member = createMember("testid");
        Member member2 = createMember("testid2");
        TeamResponse.Get createTeam1 = createTeam(member, "ownerTeam1");
        TeamResponse.Get createTeam2 = createTeam(member, "ownerTeam2");
        TeamResponse.Get inviteTeam = createTeam(member2, "memberTeam1");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(inviteTeam.getId(),member2.getUsername());
        createAndInviteMember(teamOwner, member);

        //when
        String response = mockMvc.perform(get("/api/teams/members/" + member.getUsername())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        //then
        TeamUserResponse.GetAll teamUserResponse = objectMapper.readValue(response, TeamUserResponse.GetAll.class);
        assertEquals(3, teamUserResponse.getTeamUsers().size());
        assertEquals("OWNER", teamUserResponse.getTeamUsers().get(0).getRole().name());
        assertEquals("OWNER", teamUserResponse.getTeamUsers().get(1).getRole().name());
        assertEquals("MEMBER", teamUserResponse.getTeamUsers().get(2).getRole().name());
    }
}
