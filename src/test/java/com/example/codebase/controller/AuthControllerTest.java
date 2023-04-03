package com.example.codebase.controller;

import com.example.codebase.ArtBackendApplication;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.auth.dto.LoginDTO;
import com.example.codebase.domain.auth.dto.TokenResponseDTO;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.PrepareTestInstance;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public Member createOrLoadMember() {
        Optional<Member> testMember = memberRepository.findByUsername("testid");
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy = Member.builder()
                .username("testid")
                .password(passwordEncoder.encode("1234"))
                .email("email")
                .name("test")
                .activated(true)
                .createdTime(LocalDateTime.now())
                .build();

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of("ROLE_USER"));
        memberAuthority.setMember(dummy);
        dummy.setAuthorities(Collections.singleton(memberAuthority));

        Member save = memberRepository.save(dummy);
        memberAuthorityRepository.save(memberAuthority);
        return save;
    }

    @DisplayName("로그인 API가 작동한다")
    @Test
    public void test1() throws Exception {
        Member member = createOrLoadMember();
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testid");
        loginDTO.setPassword("1234");

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDTO))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("토큰 재발급 시")
    @Test
    public void test2() throws Exception {
        createOrLoadMember();

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testid");
        loginDTO.setPassword("1234");

        String refreshToken = tokenProvider.generateToken(loginDTO).getRefreshToken();

        mockMvc.perform(
                        post("/api/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(refreshToken)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid")
    @DisplayName("사용자 로그아웃")
    @Test
    public void test3() throws Exception {
        createOrLoadMember();

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testid");
        loginDTO.setPassword("1234");

        tokenProvider.generateToken(loginDTO);

        mockMvc.perform(
                        post("/api/logout")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}