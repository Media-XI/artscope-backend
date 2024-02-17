package com.example.codebase.controller;


import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.notification.entity.NotificationType;
import com.example.codebase.domain.notification.repository.NotificationRepository;
import com.example.codebase.domain.notification.repository.NotificationSettingRepository;
import com.example.codebase.domain.notification.service.NotificationSettingService;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
public class NotificationSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationSettingRepository notificationSettingRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private NotificationRepository notificationRepository;

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
        return createOrLoadMember("testid", "ROLE_ADMIN");
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

        for (int i = 0; i < NotificationType.values().length; i++) {
            NotificationSetting notificationSetting = NotificationSetting.builder()
                    .member(dummy)
                    .notificationType(NotificationType.values()[i])
                    .isReceive(true)
                    .build();
        }

        memberRepository.save(dummy);
        return dummy;
    }


    @Transactional
    public void createNotificationSetting(Member member) {
        for (NotificationType type : NotificationType.values()) {
            NotificationSetting notificationSetting = NotificationSetting.builder()
                    .member(member)
                    .notificationType(type)
                    .isReceive(true)
                    .build();

            notificationSettingRepository.save(notificationSetting);
        }
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("알림 설정을 조회")
    @Test
    public void getNotificationSetting() throws Exception {
        // given
        Member testMember = createOrLoadMember();
        createNotificationSetting(testMember);

        mockMvc.perform(get("/api/notification-setting"))
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("알림 설정을 수정")
    @Test
    public void updateNotificationSetting() throws Exception {
        Member testMember = createOrLoadMember();
        createNotificationSetting(testMember);

        mockMvc.perform(patch("/api/notification-setting/ANNOUNCEMENT"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/notification-setting"))
                .andExpect(status().isOk());

    }
}







