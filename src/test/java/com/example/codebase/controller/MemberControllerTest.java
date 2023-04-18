package com.example.codebase.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.dto.UpdateMemberDTO;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(S3MockConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ResourceLoader resourceLoader;

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

    @BeforeAll
    static void setUp(@Autowired S3Mock s3Mock,
                      @Autowired AmazonS3 amazonS3,
                      @Autowired MockMvc mockMvc) {
        log.info("s3Mock start");
        s3Mock.start();
        amazonS3.createBucket("media-xi-art-storage");
    }

    @AfterAll
    static void tearDown(@Autowired S3Mock s3Mock,
                         @Autowired AmazonS3 amazonS3) {
        log.info("s3Mock stop");
        amazonS3.deleteBucket("media-xi-art-storage");
        s3Mock.stop();
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

    private byte[] createImageFile() throws IOException {
        File file = resourceLoader.getResource("classpath:test/img.jpg").getFile();
        return Files.readAllBytes(file.toPath());
    }


    @DisplayName("회원가입 API가 작동한다")
    @Test
    void test1() throws Exception {
        CreateMemberDTO dto = new CreateMemberDTO();
        dto.setEmail("test213@test.com");
        dto.setName("test1");
        dto.setUsername("test213");
        dto.setPassword("1234");

        mockMvc.perform(
                        post("/api/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("관리자 회원가입 API가 작동한다")
    @Test
    void 관리자_가입() throws Exception {
        CreateMemberDTO dto = new CreateMemberDTO();
        dto.setEmail("test23@test.com");
        dto.setName("test");
        dto.setUsername("test23");
        dto.setPassword("1234");

        mockMvc.perform(
                        post("/api/members/admin")
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

    @DisplayName("사용자 프로필 조회 시")
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

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("내 프로필 정보 수정 시")
    @Test
    void 프로필_수정() throws Exception {
        createOrLoadMember();

        UpdateMemberDTO dto = new UpdateMemberDTO();
        dto.setName("test1");
        dto.setIntroduction("소개 수정");
        dto.setHistory("history 수정");

        mockMvc.perform(
                        put(String.format("/api/members/testid1"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("내 프로필 이미지 수정 시")
    @Test
    void 프로필_이미지_수정() throws Exception {
        createOrLoadMember();

        MockMultipartFile file = new MockMultipartFile("profile", "test.jpg", "image/jpg", createImageFile());

        mockMvc.perform(
                    multipart(String.format("/api/members/testid1/picture"))
                            .file(file)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })

                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("회원 탈퇴 시")
    @Test
    void 회원_탈퇴() throws Exception {
        createOrLoadMember();

        mockMvc.perform(
                        delete(String.format("/api/members/testid1"))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("이메일 중복 체크 시")
    @Test
    void 이메일_중복_체크() throws Exception {
        createOrLoadMember();

        mockMvc.perform(
                        get(String.format("/api/members/email/%s", "email1"))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("아이디 중복 체크 시")
    @Test
    void 아이디_중복_체크() throws Exception {
        createOrLoadMember();

        mockMvc.perform(
                        get(String.format("/api/members/username/%s", "testid1"))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("사용자 아티스트 승인 시")
    @Test
    void 아티스트_승인() throws Exception {
        createOrLoadMember();

        String status = "APPROVED";

        mockMvc.perform(
                        put(String.format("/api/members/artist/%s?status=%s", "testid1", status))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("사용자 잘못된 값으로 아티스트 승인 시")
    @Test
    void 아티스트_승인_잘못된값() throws Exception {
        createOrLoadMember();

        String status = "asd";

        mockMvc.perform(
                        put(String.format("/api/members/artist/%s?status=%s", "testid1", status))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

}