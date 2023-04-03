package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public Member createOrLoadMember() {
        return createOrLoadMember(1);
    }

    public Member createOrLoadMember(int index) {
        Optional<Member> testMember = memberRepository.findByUsername("testid" + index);
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy = Member.builder()
                .username("testid" + index)
                .password(passwordEncoder.encode("1234"))
                .email("email" + index)
                .name("test" + index)
                .activated(true)
                .createdTime(LocalDateTime.now().plusSeconds(index))
                .build();

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of("ROLE_USER"));
        memberAuthority.setMember(dummy);
        dummy.setAuthorities(Collections.singleton(memberAuthority));

        return memberRepository.save(dummy);
    }

    @DisplayName("회원가입 API가 작동한다")
    @Test
    void test1() throws Exception {
        CreateMemberDTO dto = new CreateMemberDTO();
        dto.setEmail("test23@test.com");
        dto.setName("test");
        dto.setUsername("test23");
        dto.setPassword("1234");

        mockMvc.perform(
                        post("/api/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("아티스트 정보 입력 API가 작동한다")
    @WithMockCustomUser(username = "testid1", role = "USER")
    @Test
    void test2() throws Exception {
        createOrLoadMember();

        CreateArtistMemberDTO dto = new CreateArtistMemberDTO();
        dto.setIntroduction("소개");
        dto.setSnsUrl("https://localhost/");
        dto.setWebsiteUrl("https://localhost/");
        dto.setHistory("연혁");

        mockMvc.perform(
                        post("/api/members/artist")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("전체 회원 조회 시")
    @Test
    void test3() throws Exception {
        createOrLoadMember(1);
        createOrLoadMember(2);
        createOrLoadMember(3);

        mockMvc.perform(
                        get("/api/members")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("로그인한 사용자 프로필 조회 시")
    @Test
    void test4() throws Exception {
        createOrLoadMember();

        mockMvc.perform(
                        get("/api/members/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("관리자가 사용자 프로필 조회 시")
    @Test
    void test5() throws Exception {
        createOrLoadMember();

        mockMvc.perform(
                        get(String.format("/api/members/%s", "testid1"))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}