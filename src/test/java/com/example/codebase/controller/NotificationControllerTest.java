package com.example.codebase.controller;


import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.notification.dto.NotificationMessageRequest;
import com.example.codebase.domain.notification.entity.Notification;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.notification.entity.NotificationType;
import com.example.codebase.domain.notification.repository.NotificationRepository;
import com.example.codebase.domain.notification.service.NotificationService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.codebase.domain.notification.entity.NotificationType.ANNOUNCEMENT;
import static com.example.codebase.domain.notification.entity.NotificationType.NEW_FOLLOWER;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Transactional
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

    public Notification createOrLoadNotification(Member member) {
        return createOrLoadNotification(member, ANNOUNCEMENT);
    }

    public Notification createOrLoadNotification(Member member, NotificationType type) {
        return createOrLoadNotification(member, "알림", type);
    }

    @Transactional
    public Notification createOrLoadNotification(Member member, String message, NotificationType type) {
        Notification notification = Notification.of(member, message, type);
        return notificationRepository.save(notification);
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("공지사항 공지 성공")
    @Test
    public void 공지사항_성공() throws Exception {
        createOrLoadMember();
        NotificationMessageRequest messageRequest = new NotificationMessageRequest("공지사항", "공지사항입니다.", ANNOUNCEMENT);


        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("공지사항 공지 권한으로 실패시")
    @Test
    public void 공지사항_실패() throws Exception {
        createOrLoadMember();
        NotificationMessageRequest messageRequest = new NotificationMessageRequest("팔로우", "팔로우입니다.", NEW_FOLLOWER);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isForbidden());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("공지사항 공지 잘못된 타입으로 실패시")
    @Test
    public void 공지사항_잘못된_타입_실패시() throws Exception {
        createOrLoadMember();
        NotificationMessageRequest messageRequest = new NotificationMessageRequest("팔로우", "팔로우입니다.", ANNOUNCEMENT);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isForbidden());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("알림 목록 조회 성공시")
    @Test
    public void 알림_목록_조회_성공() throws Exception {
        Member member = createOrLoadMember();
        createOrLoadNotification(member);

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());

    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("알림 읽음 처리 성공시")
    @Test
    public void 알림_읽음_처리_성공() throws Exception {
        Member member = createOrLoadMember();
        Notification notification = createOrLoadNotification(member, "알림1", ANNOUNCEMENT);

        mockMvc.perform(put("/api/notifications/{notificationId}", notification.getNotificationId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());

    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("알림 전체 읽음 처리 성공시")
    @Test
    public void 알림_전체_읽음_처리_성공() throws Exception {
        Member member = createOrLoadMember();
        createOrLoadNotification(member, "알림1", ANNOUNCEMENT);
        createOrLoadNotification(member, "알림2", ANNOUNCEMENT);
        createOrLoadNotification(member, "알림3", ANNOUNCEMENT);
        createOrLoadNotification(member, "알림4", ANNOUNCEMENT);


        mockMvc.perform(put("/api/notifications"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("알림 삭제 성공시")
    @Test
    public void 알림_삭제_성공() throws Exception {
        Member member = createOrLoadMember();
        Notification notification = createOrLoadNotification(member, "알림1", ANNOUNCEMENT);

        mockMvc.perform(delete("/api/notifications/{notificationId}", notification.getNotificationId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testuser", role = "USER")
    @DisplayName("다른 사람의 알림을 삭제 요청시")
    @Test
    public void 타인_알림_삭제_요청시() throws Exception {
        createOrLoadMember("testuser", "ROLE_USER");
        Member member = createOrLoadMember();
        Notification notification = createOrLoadNotification(member, "알림1", ANNOUNCEMENT);

        mockMvc.perform(delete("/api/notifications/{notificationId}", notification.getNotificationId()))
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("알림 전체 삭제 시")
    @Test
    public void 알림_전체_삭제() throws Exception {
        Member member = createOrLoadMember();
        createOrLoadNotification(member, "알림1", ANNOUNCEMENT);
        createOrLoadNotification(member, "알림2", ANNOUNCEMENT);
        createOrLoadNotification(member, "알림3", ANNOUNCEMENT);
        createOrLoadNotification(member, "알림4", ANNOUNCEMENT);

        mockMvc.perform(delete("/api/notifications"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("알림 읽음 처리한 알림 다시 읽음 처리를 요청할시")
    @Test
    public void 알림_읽음_처리를_다시_요청할시() throws Exception {
        Member member = createOrLoadMember();
        Notification notification = createOrLoadNotification(member, "알림1", ANNOUNCEMENT);

        mockMvc.perform(put("/api/notifications/{notificationId}", notification.getNotificationId()))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/notifications/{notificationId}", notification.getNotificationId()))
                .andExpect(status().isBadRequest());
    }
}
