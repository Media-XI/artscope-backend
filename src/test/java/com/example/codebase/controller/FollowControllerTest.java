package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.follow.dto.FollowRequest;
import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.repository.FollowRepository;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.repository.TeamRepository;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

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
class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private TeamRepository teamRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Member createOrLoadMember() {
        return createOrLoadMember("testid", "ROLE_USER");
    }

    public Member createOrLoadMember(String username, String... authorities) {
        Optional<Member> testMember = memberRepository.findByUsername(username);
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy = Member.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username + "@test.com")
                .name(username)
                .activated(true)
                .createdTime(LocalDateTime.now())
                .build();

        for (String authority : authorities) {
            MemberAuthority memberAuthority = new MemberAuthority();
            memberAuthority.setAuthority(Authority.of(authority));
            memberAuthority.setMember(dummy);
        }

        NotificationSetting notificationSetting = NotificationSetting.builder().member(dummy).build();
        dummy.setNotificationSetting(notificationSetting);

        return memberRepository.save(dummy);
    }

    public Team createOrLoadTeam() {
        return createOrLoadTeam("testteam");
    }

    public Team createOrLoadTeam(String teamName) {
        Optional<Team> team = teamRepository.findByName(teamName);
        if (team.isPresent()) {
            return team.get();
        }

        Team dummy = Team.builder()
                .name(teamName)
                .description("우리 회사는 최고의 국내 기업이고 현재는 어쩌구 저쩌구 연매출은 어떻고 뭐 복지는 좋고 말고")
                .backgroundImage("test")
                .profileImage("test")
                .address("test")
                .createdTime(LocalDateTime.now())
                .build();

        return teamRepository.save(dummy);
    }

    public Follow createOrLoadFollow(Member follower, Member following) {
        return followRepository.save(Follow.of(follower, following));
    }

    public Follow createOrLoadFollow(Member follower, Team following) {
        return followRepository.save(Follow.of(follower, following));
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("상대방 팔로우 성공")
    @Test
    public void 팔로우_성공() throws Exception {
        createOrLoadMember();
        Member followUser = createOrLoadMember("followUser", "ROLE_USER");

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:member:" + followUser.getUsername());

        mockMvc.perform(
                        post("/api/follow")
                                .param("action", "follow")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팔로우 중인데 다시 팔로잉 시")
    @Test
    public void 다시_팔로잉() throws Exception {
        Member member = createOrLoadMember("testid", "ROLE_USER");
        Member member2 = createOrLoadMember("testid2", "ROLE_USER");
        createOrLoadFollow(member , member2);

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:member:" + member2.getUsername());

        String content = mockMvc.perform(post("/api/follow")
                        .param("action", "follow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("이미 팔로우 중입니다."));
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("자기 자신을 팔로우 할시")
    @Test
    public void 자기_자신을_팔로우_할떄() throws Exception {
        createOrLoadMember("testid", "ROLE_USER");

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:member:testid");

        String content = mockMvc.perform(post("/api/follow")
                        .param("action", "follow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("자기 자신을 팔로우 할 수 없습니다"));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("자기 자신을 언 팔로우 할시")
    @Test
    public void 자기_자신을_언팔로우_할떄() throws Exception {
        createOrLoadMember("testid", "ROLE_USER");

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:member:testid");

        String content = mockMvc.perform(post("/api/follow")
                        .param("action", "unfollow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("자기 자신을 언팔로우 할 수 없습니다"));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팔로우 중이 아닐때 언팔로우를 시도할시")
    @Test
    public void 언팔로우_실패() throws Exception {
        createOrLoadMember();
        Member followUser = createOrLoadMember("followUserasda", "ROLE_USER");

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:member:" + followUser.getUsername());

        String content = mockMvc.perform(post("/api/follow")
                        .param("action", "unfollow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("팔로우 중이 아닙니다."));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("상대방 언팔로우 성공")
    @Test
    public void 언팔로우_성공() throws Exception {
        createOrLoadMember();
        Member followUser = createOrLoadMember("unfollowUser", "ROLE_USER");

        createOrLoadFollow(createOrLoadMember(), followUser);

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:member:" + followUser.getUsername());

        mockMvc.perform(post("/api/follow")
                        .param("action", "unfollow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 팔로잉 시")
    @Test
    public void 팀_팔로잉() throws Exception {
        createOrLoadMember();

        Team team = createOrLoadTeam();

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:team:" + team.getId());

        mockMvc.perform(post("/api/follow")
                        .param("action", "follow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 팔로잉 중인데 다시 팔로잉할 시")
    @Test
    public void 팀_팔로우_실패() throws Exception {
        Member member = createOrLoadMember();
        Team team = createOrLoadTeam();

        createOrLoadFollow(member, team);

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:team:" + team.getId());

        String content = mockMvc.perform(post("/api/follow")
                        .param("action", "follow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("이미 팔로우 중입니다."));
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 언팔로우 시")
    @Test
    public void 팀_언팔로잉() throws Exception {
        Member member = createOrLoadMember();
        Team team = createOrLoadTeam();

        createOrLoadFollow(member,team);

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:team:" + team.getId());

        mockMvc.perform(post("/api/follow")
                        .param("action", "unfollow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팀 팔로우 중이 아닐때 언팔로우를 시도할시")
    @Test
    public void 팀_언팔로우_실패() throws Exception {
        createOrLoadMember();
        Team team = createOrLoadTeam();

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:team:" + team.getId());

        String content = mockMvc.perform(post("/api/follow")
                        .param("action", "unfollow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(content.contains("팔로우 중이 아닙니다."));
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팔로우 API를 연속으로 호출할 시")
    @Test
    public void 팔로우API_연속호출() throws Exception {
        // given
        Member member = createOrLoadMember();
        Member followUser = createOrLoadMember("followUser", "ROLE_USER");

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:member:" + followUser.getUsername());

        mockMvc.perform(post("/api/follow")
                        .param("action", "follow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isCreated());

        // when
        String response = mockMvc.perform(post("/api/follow")
                        .param("action", "follow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        // then
        assertTrue(response.contains("잠시 후 다시 시도해주세요."));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("잘못된 URN 형식으로 요청 시")
    @Test
    public void 잘못된_URN() throws Exception {
        // given
        Member member = createOrLoadMember();
        Team team = createOrLoadTeam();
        createOrLoadFollow(member,team);

        FollowRequest.Create request = new FollowRequest.Create();

        // when1 -> urn:teamdd:1
        request.setUrn("urn:teamdd:" + team.getId());
        String response1 = mockMvc.perform(post("/api/follow")
                        .param("action", "unfollow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(response1.contains("유효하지 않은 EntityUrn 입니다."));

        // when2 -> asdsd
        request.setUrn("aadsd");
        String response2 = mockMvc.perform(post("/api/follow")
                        .param("action", "unfollow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertTrue(response2.contains("올바른 URN 형식이 아닙니다."));

        // when3 -> urn::asd
        request.setUrn("urn::asd");
        String response3 = mockMvc.perform(post("/api/follow")
                        .param("action", "unfollow")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertTrue(response3.contains("올바른 URN 형식이 아닙니다."));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("잘못된 Action으로 API 호출 시")
    @Test
    public void 잘못된_ACTION () throws Exception {
        Member member = createOrLoadMember();
        Team team = createOrLoadTeam();

        FollowRequest.Create request = new FollowRequest.Create();
        request.setUrn("urn:team:" + team.getId());

        // when1
        String response1 = mockMvc.perform(post("/api/follow")
                        .param("action", "cc")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(response1.contains("잘못된 action 값입니다."));

        // when2
        String response2 = mockMvc.perform(post("/api/follow")
                        .param("action", "")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(response2.contains("잘못된 action 값입니다."));
    }

    @DisplayName("비 로그인 상태로 팔로잉 목록 조회시")
    @Test
    public void 비로그인_팔로잉_목록_조회() throws Exception {
        Member member = createOrLoadMember();
        Member followUser = createOrLoadMember("followUser1", "ROLE_USER");
        Member followUser2 = createOrLoadMember("followUser2", "ROLE_USER");
        Member followUser3 = createOrLoadMember("followUser3", "ROLE_USER");
        Member followUser4 = createOrLoadMember("followUser4", "ROLE_USER");
        Member followUser5 = createOrLoadMember("followUser5", "ROLE_USER");
        Team team = createOrLoadTeam();

        createOrLoadFollow(member, followUser4);
        createOrLoadFollow(member, followUser);
        createOrLoadFollow(member, followUser2);
        createOrLoadFollow(member, followUser3);
        createOrLoadFollow(member, followUser5);
        createOrLoadFollow(member, team);

        mockMvc.perform(get(String.format("/api/follow/%s/following", member.getUsername())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("로그인 상태로 팔로잉 목록 조회시")
    @Test
    public void 로그인_상태로_팔로잉_목록_조회() throws Exception {
        Member loginUser = createOrLoadMember();

        Member member = createOrLoadMember("targetUser", "ROLE_USER");
        Member followUser1 = createOrLoadMember("followUser1", "ROLE_USER");
        Member followUser2 = createOrLoadMember("followUser2", "ROLE_USER");
        Member followUser3 = createOrLoadMember("followUser3", "ROLE_USER");
        Member followUser4 = createOrLoadMember("followUser4", "ROLE_USER");
        Member followUser5 = createOrLoadMember("followUser5", "ROLE_USER");
        Team team = createOrLoadTeam();

        createOrLoadFollow(member, followUser4);
        createOrLoadFollow(member, followUser1);
        createOrLoadFollow(member, followUser2);
        createOrLoadFollow(member, followUser3);
        createOrLoadFollow(member, followUser5);
        createOrLoadFollow(member, team);

        createOrLoadFollow(member, loginUser);

        createOrLoadFollow(loginUser, followUser1);
        createOrLoadFollow(loginUser, followUser4);
        createOrLoadFollow(loginUser, followUser5);
        createOrLoadFollow(loginUser, team);

        mockMvc.perform(get(String.format("/api/follow/%s/following", member.getUsername())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("로그인 상태로 팔로잉 목록 조회시 - 팀 팔로잉 여부")
    @Test
    public void 로그인_상태로_팔로잉_목록_조회2() throws Exception {
        Member loginUser = createOrLoadMember();

        Member member = createOrLoadMember("targetUser", "ROLE_USER");
        Member followUser1 = createOrLoadMember("followUser1", "ROLE_USER");
        Member followUser2 = createOrLoadMember("followUser2", "ROLE_USER");
        Team team = createOrLoadTeam();
        Team team2 = createOrLoadTeam("team2");

        createOrLoadFollow(member, followUser1);
        createOrLoadFollow(member, followUser2);
        createOrLoadFollow(member, team);
        createOrLoadFollow(member, team2);
        createOrLoadFollow(member, loginUser);

        createOrLoadFollow(loginUser, followUser1);
        createOrLoadFollow(loginUser, team);

        mockMvc.perform(get(String.format("/api/follow/%s/following", member.getUsername())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("비 로그인 상태로 팔로워 목록 조회시")
    @Test
    public void 비로그인_팔로워_목록_조회() throws Exception {
        Member member = createOrLoadMember();

        Member followUser = createOrLoadMember("followUser1", "ROLE_USER");
        Member followUser2 = createOrLoadMember("followUser2", "ROLE_USER");
        Member followUser3 = createOrLoadMember("followUser3", "ROLE_USER");
        Member followUser4 = createOrLoadMember("followUser4", "ROLE_USER");
        Member followUser5 = createOrLoadMember("followUser5", "ROLE_USER");

        createOrLoadFollow(followUser4, member);
        createOrLoadFollow(followUser, member);
        createOrLoadFollow(followUser2, member);
        createOrLoadFollow(followUser3, member);
        createOrLoadFollow(followUser5, member);

        mockMvc.perform(get(String.format("/api/follow/%s/follower", member.getUsername())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("로그인 상태로 팔로워 목록 조회시")
    @Test
    public void 로그인_상태로_팔로워_목록_조회() throws Exception {
        Member loginUser = createOrLoadMember();

        Member member = createOrLoadMember("targetUser", "ROLE_USER");
        Member followUser1 = createOrLoadMember("followUser1", "ROLE_USER");
        Member followUser2 = createOrLoadMember("followUser2", "ROLE_USER");
        Member followUser3 = createOrLoadMember("followUser3", "ROLE_USER");
        Member followUser4 = createOrLoadMember("followUser4", "ROLE_USER");
        Member followUser5 = createOrLoadMember("followUser5", "ROLE_USER");

        createOrLoadFollow(followUser4, member);
        createOrLoadFollow(followUser1, member);
        createOrLoadFollow(followUser2, member);
        createOrLoadFollow(followUser3, member);
        createOrLoadFollow(followUser5, member);

        createOrLoadFollow(loginUser, followUser1);
        createOrLoadFollow(loginUser, followUser4);
        createOrLoadFollow(loginUser, followUser2);
        createOrLoadFollow(loginUser, followUser3);

        createOrLoadFollow(followUser4, member);
        createOrLoadFollow(followUser2, member);
        createOrLoadFollow(followUser3, member);

        createOrLoadFollow(loginUser, member);

        mockMvc.perform(get(String.format("/api/follow/%s/follower", member.getUsername())))
                .andDo(print())
                .andExpect(status().isOk());
    }
}