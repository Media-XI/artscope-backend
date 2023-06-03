package com.example.codebase.controller;

import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.s3.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(S3MockConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private MemberRepository memberRepository;

    private ObjectMapper objectMapper = new ObjectMapper();
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }



    @DisplayName("이메일 인증 API ")
    @Test
    void 이메일_인증_API () throws Exception {
        Member dummy = Member.builder()
                .username("testid")
                .password(passwordEncoder.encode("1234"))
                .email("shonn2323@gmail.com")
                .name("test")
                .activated(false)
                .createdTime(LocalDateTime.now())
                .build();

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of("ROLE_GUEST"));
        memberAuthority.setMember(dummy);
        dummy.setAuthorities(Collections.singleton(memberAuthority));

        memberRepository.save(dummy);
        memberAuthorityRepository.save(memberAuthority);

        String email = "shonn2323@gmail.com";

        mockMvc.perform(
                        post("/api/mail/authenticate")
                                .param("email", email)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}