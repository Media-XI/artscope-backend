package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.repository.FollowRepository;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    public Follow createOrLoadFollow(Member follower, Member following) {
        return followRepository.save(Follow.of(follower, following));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("상대방 팔로우 성공")
    @Test
    public void 팔로우_성공() throws Exception {
        createOrLoadMember();
        Member followUser = createOrLoadMember("followUser", "ROLE_USER");

        mockMvc.perform(post("/api/follow/" + followUser.getUsername()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("자기 자신을 팔로우 할시")
    @Test
    public void 자기_자신을_팔로우_할떄() throws Exception {
        createOrLoadMember("testid", "ROLE_CURATOR");

        mockMvc.perform(post(String.format("/api/follow/%s", "testid")))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("자기 자신을 언 팔로우 할시")
    @Test
    public void 자기_자신을_언팔로우_할떄() throws Exception {
        createOrLoadMember("testid", "ROLE_CURATOR");

        mockMvc.perform(delete(String.format("/api/follow/%s", "testid")))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("팔로우 중이 아닐때 언팔로우를 시도할시")
    @Test
    public void 언팔로우_실패() throws Exception {
        createOrLoadMember();
        Member followUser = createOrLoadMember("followUser", "ROLE_USER");

        mockMvc.perform(delete(String.format("/api/follow/" + followUser.getUsername())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("상대방 언팔로우 성공")
    @Test
    public void 언팔로우_성공() throws Exception {
        createOrLoadMember();
        Member followUser = createOrLoadMember("followUser", "ROLE_USER");

        createOrLoadFollow(createOrLoadMember(), followUser);

        mockMvc.perform(delete(String.format("/api/follow/" + followUser.getUsername())))
                .andExpect(status().isNoContent());
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

        createOrLoadFollow(member, followUser4);
        createOrLoadFollow(member, followUser);
        createOrLoadFollow(member, followUser2);
        createOrLoadFollow(member, followUser3);
        createOrLoadFollow(member, followUser5);


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
        Member followUser6 = createOrLoadMember("followUser6", "ROLE_USER");
        Member followUser7 = createOrLoadMember("followUser7", "ROLE_USER");
        Member followUser8 = createOrLoadMember("followUser8", "ROLE_USER");
        Member followUser9 = createOrLoadMember("followUser9", "ROLE_USER");
        Member followUser10 = createOrLoadMember("followUser10", "ROLE_USER");
        Member followUser11 = createOrLoadMember("followUser11", "ROLE_USER");
        Member followUser12 = createOrLoadMember("followUser12", "ROLE_USER");
        Member followUser13 = createOrLoadMember("followUser13", "ROLE_USER");

        createOrLoadFollow(member, followUser4);
        createOrLoadFollow(member, followUser1);
        createOrLoadFollow(member, followUser2);
        createOrLoadFollow(member, followUser3);
        createOrLoadFollow(member, followUser5);
        createOrLoadFollow(member, followUser6);
        createOrLoadFollow(member, followUser7);
        createOrLoadFollow(member, followUser8);
        createOrLoadFollow(member, followUser9);
        createOrLoadFollow(member, followUser10);
        createOrLoadFollow(member, followUser11);
        createOrLoadFollow(member, followUser12);
        createOrLoadFollow(member, followUser13);

        createOrLoadFollow(member, loginUser);

        createOrLoadFollow(loginUser, followUser1);
        createOrLoadFollow(loginUser, followUser4);
        createOrLoadFollow(loginUser, followUser6);

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
        Member followUser6 = createOrLoadMember("followUser6", "ROLE_USER");
        Member followUser7 = createOrLoadMember("followUser7", "ROLE_USER");
        Member followUser8 = createOrLoadMember("followUser8", "ROLE_USER");
        Member followUser9 = createOrLoadMember("followUser9", "ROLE_USER");
        Member followUser10 = createOrLoadMember("followUser10", "ROLE_USER");
        Member followUser11 = createOrLoadMember("followUser11", "ROLE_USER");

        createOrLoadFollow(followUser4, member);
        createOrLoadFollow(followUser1, member);
        createOrLoadFollow(followUser2, member);
        createOrLoadFollow(followUser3, member);
        createOrLoadFollow(followUser5, member);
        createOrLoadFollow(followUser6, member);

        createOrLoadFollow(followUser7, member);
        createOrLoadFollow(followUser8, member);

        createOrLoadFollow(loginUser, followUser1);
        createOrLoadFollow(loginUser, followUser4);
        createOrLoadFollow(loginUser, followUser6);
        createOrLoadFollow(loginUser, followUser2);
        createOrLoadFollow(loginUser, followUser3);

        createOrLoadFollow(followUser9, member);
        createOrLoadFollow(followUser10, member);
        createOrLoadFollow(followUser11, member);

        createOrLoadFollow(loginUser, member);

        mockMvc.perform(get(String.format("/api/follow/%s/follower", member.getUsername())))
                .andDo(print())
                .andExpect(status().isOk());
    }
}