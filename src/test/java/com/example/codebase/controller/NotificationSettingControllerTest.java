package com.example.codebase.controller;


import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.member.service.MemberService;
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
    private MemberService memberService;

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
        return createOrLoadMember("testid");
    }

    public Member createOrLoadMember(String username) {
        CreateMemberDTO createMemberDTO = new CreateMemberDTO();
        createMemberDTO.setUsername(username);
        createMemberDTO.setPassword("password");
        createMemberDTO.setName("name");
        createMemberDTO.setEmail("email");
        createMemberDTO.setAllowEmailReceive(true);

        memberService.createMember(createMemberDTO);
        return memberService.getEntity(username);
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("알림 설정을 조회")
    @Test
    public void 알림_설정_조회() throws Exception {
        Member testMember = createOrLoadMember();

        mockMvc.perform(get("/api/notification-setting"))
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("알림 설정 수정")
    @Test
    public void 알림_설정_수정시() throws Exception {
        Member testMember = createOrLoadMember();

        mockMvc.perform(patch("/api/notification-setting/ANNOUNCEMENT"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/notification-setting"))
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("알림 설정 수정시 잘못된 값을 입력시")
    @Test
    public void 알림_수정시_잘못된_값_입력시() throws Exception {
        Member testMember = createOrLoadMember();

        mockMvc.perform(patch("/api/notification-setting/잘못된값"))
                .andExpect(status().isBadRequest());
    }


}







