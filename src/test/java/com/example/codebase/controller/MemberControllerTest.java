package com.example.codebase.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.services.s3.AmazonS3;
import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.dto.CreateCuratorMemberDTO;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.dto.UpdateMemberDTO;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.findify.s3mock.S3Mock;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

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

    @BeforeAll
    static void setUp(@Autowired S3Mock s3Mock,
                      @Autowired AmazonS3 amazonS3,
                      @Autowired MockMvc mockMvc) {
        log.info("s3Mock start");
        s3Mock.start();
        amazonS3.createBucket("media-xi-art-storage");
  }

  @AfterAll
  static void tearDown(@Autowired S3Mock s3Mock, @Autowired AmazonS3 amazonS3) {
    log.info("s3Mock stop");
    amazonS3.deleteBucket("media-xi-art-storage");
    s3Mock.stop();
  }

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  public Member createOrLoadMember() {
    return createOrLoadMember(1);
  }

  public Member createOrLoadMember(int index) {
    Optional<Member> testMember = memberRepository.findByUsername("testid" + index);
    if (testMember.isPresent()) {
      return testMember.get();
    }

    Member dummy =
        Member.builder()
            .username("testid" + index)
            .password(passwordEncoder.encode("1234"))
            .email("email" + index + ".com")
            .name("test" + index)
            .companyName("company" + index)
            .activated(true)
            .createdTime(LocalDateTime.now().plusSeconds(index))
            .build();

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of("ROLE_USER"));
        memberAuthority.setMember(dummy);
        dummy.addAuthority(memberAuthority);

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

    @WithMockCustomUser(username = "admin", role = "ADMIN")
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
        dto.setWebsiteUrl("https://localhost/");
        dto.setSnsUrl("https://localhost/");
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
    @DisplayName("내 프로필 사진 수정시 이미지 타입이 아닐 시")
    @Test
    void 프로필_이미지가_아닐시() throws Exception {
        createOrLoadMember();

        MockMultipartFile file = new MockMultipartFile("profile", "test.mp3", "audio/mp3", "asd".getBytes());

        mockMvc.perform(
                        multipart(String.format("/api/members/testid1/picture"))
                                .file(file)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })

                )
                .andExpect(status().isUnsupportedMediaType())
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
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("아이디 중복 체크 시")
    @Test
    void 아이디_중복_체크() throws Exception {
        Member member = createOrLoadMember();

        mockMvc.perform(
                        get(String.format("/api/members/username/%s", member.getUsername()))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("사용자 아티스트 승인 시")
    @Test
    void 아티스트_승인() throws Exception {
        createOrLoadMember();

        String status = "ARTIST";

        mockMvc.perform(
                        put(String.format("/api/members/artist/%s?status=%s", "testid1", status))
                )
                .andDo(print())
                .andExpect(status().isOk());
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
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("아이디 변경 시")
    @Test
    void 아이디_변경() throws Exception {
        Member loadMember = createOrLoadMember();

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember.getUsername(), "newid"))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("이미 사용중인 아이디로 변경 시")
    @Test
    void 사용중인_아이디로_변경() throws Exception {
        Member loadMember1 = createOrLoadMember();
        Member loadMember2 = createOrLoadMember(2);

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember1.getUsername(),
                                loadMember2.getUsername()))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("아이디 변경 시 유효성 검증 확인")
    @Test
    void 아이디_검증() throws Exception {
        Member loadMember1 = createOrLoadMember();

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember1.getUsername(), "1"))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember1.getUsername(), "한글"))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember1.getUsername(),
                                "asdasdasdsadasdasdasdsa"))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("비밀번호 변경 시")
    @Test
    void 비밀번호_변경() throws Exception {
        Member loadMember = createOrLoadMember();

        mockMvc.perform(
                        put(String.format("/api/members/%s/password", loadMember.getUsername()))
                                .param("newPassword", "newpassword123!")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("유효하지 않은 이메일로 가입 시")
    @Test
    void non_email_create_mebmer() throws Exception {
        CreateMemberDTO dto = new CreateMemberDTO();
        dto.setEmail("qwer@gmailcom");
        dto.setName("test1");
        dto.setUsername("test213");
        dto.setPassword("1234");

        mockMvc.perform(
                        post("/api/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("기획자 정보 입력 시")
    @Test
    void create_curator() throws Exception {
        Member member = createOrLoadMember();
        CreateCuratorMemberDTO dto = new CreateCuratorMemberDTO();
        dto.setIntroduction("소개");
        dto.setHistory("연혁");
        dto.setWebsiteUrl("https://localhost/");
        dto.setSnsUrl("https://localhost/");
        dto.setCompanyName("회사 이름");
        dto.setCompanyRole("기획자");
    dto.setUsername(member.getUsername());

    mockMvc
        .perform(
            post("/api/members/curator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @DisplayName("이메일로 유저 리스트 조회")
  @Test
  void 이메일로_유저_리스트_조회() throws Exception {
    Member member = createOrLoadMember(2);

    mockMvc
        .perform(get("/api/members/search/{email}", member.getEmail()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("이름으로 유저 리스트 조회")
  @Test
  void 유저_이름으로_유저_리스트_조회() throws Exception {
    Member member = createOrLoadMember(2);

    mockMvc
        .perform(get("/api/members/search/{username}", member.getUsername()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("이름으로 유저 리스트 조회")
  @Test
  void 이름으로_유저_리스트_조회() throws Exception {

    createOrLoadMember(1);
    createOrLoadMember(2);
    createOrLoadMember(3);
    createOrLoadMember(4);

    mockMvc
        .perform(get("/api/members/search/{name}", "test"))
        .andDo(print())
        .andExpect(status().isOk());
  }
}
