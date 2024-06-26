package com.example.codebase.controller;

import co.elastic.clients.util.ContentType;
import com.amazonaws.services.s3.AmazonS3;
import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.dto.*;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.entity.RoleStatus;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.team.dto.TeamRequest;
import com.example.codebase.domain.team.dto.TeamResponse;

import com.example.codebase.domain.team.dto.TeamUserRequest;
import com.example.codebase.domain.team.dto.TeamUserResponse;
import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.repository.TeamUserRepository;
import com.example.codebase.domain.team.service.TeamService;
import com.example.codebase.domain.team.service.TeamUserService;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamUserService teamUserService;

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
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Member createOrLoadMember() {
        return createOrLoadMember(1, RoleStatus.NONE);
    }

    public Member createOrLoadMember(int idx) {
        return createOrLoadMember(idx, RoleStatus.NONE);
    }

    public Member createOrLoadMember(int index, RoleStatus role) {
        Optional<Member> testMember = memberRepository.findByUsername("testid" + index);
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy = Member.builder()
                .username("testid" + index)
                .password(passwordEncoder.encode("1234"))
                .email("email" + index + "@test.com")
                .name("test" + index)
                .companyName("company" + index)
                .roleStatus(role)
                .activated(true)
                .allowEmailReceive(true)
                .allowEmailReceiveDatetime(LocalDateTime.now())
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

    public Team createTeam(Member member, String name) {
        return teamService.createTeam(createTeamRequest(name), member);
    }

    public TeamRequest.Create createTeamRequest(String name) {
        return new TeamRequest.Create(
                name,
                "팀 주소",
                "http://test.com/profile.jpg",
                "http://test.com/background.jpg",
                "팀소개",
                "자신의 포지션, 직급");
    }

    public void createAndInviteMember(TeamUser loginUser, Member inviteMember) {
        TeamUserRequest.Create request = new TeamUserRequest.Create(
                "팀원"
        );
        teamUserService.addTeamUser(loginUser, inviteMember, request);
    }

    @DisplayName("회원가입 API가 작동한다")
    @Test
    void test1() throws Exception {
        CreateMemberDTO dto = new CreateMemberDTO();
        dto.setEmail("test213@test.com");
        dto.setName("test1");
        dto.setUsername("test213");
        dto.setPassword("1234");
        dto.setAllowEmailReceive(true);

        mockMvc.perform(
                        post("/api/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
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
        dto.setAllowEmailReceive(true);

        mockMvc.perform(
                        post("/api/members/admin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
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
                                .content(objectMapper.writeValueAsString(dto)))
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
                                .param("page", "0")
                                .param("size", "10")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("사용자 프로필 조회 시")
    @Test
    void test5() throws Exception {
        createOrLoadMember();

        mockMvc.perform(
                        get(String.format("/api/members/%s", "testid1"))
                                .contentType(MediaType.APPLICATION_JSON))
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
                        put("/api/members/testid1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("내 프로필 이미지 수정 시")
    @Test
    void 프로필_이미지_수정() throws Exception {
        Member member = createOrLoadMember();

        ProfileUrlDTO profileUrlDTO = new ProfileUrlDTO("https://asdfasdf.com");

        mockMvc.perform(
                        put("/api/members/%s/picture".formatted(member.getUsername()))
                                .contentType(ContentType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(profileUrlDTO))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("내 프로필 사진 수정시 URL 타입이 아닐 시")
    @Test
    void 프로필_이미지가_아닐시() throws Exception {
        Member member = createOrLoadMember();

        ProfileUrlDTO profileUrlDTO = new ProfileUrlDTO("asdfasdf.com");

        mockMvc.perform(
                        put("/api/members/%s/picture".formatted(member.getUsername()))
                                .contentType(ContentType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(profileUrlDTO))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        ProfileUrlDTO profileUrlDTO2 = new ProfileUrlDTO("http://asdfasdf.com");

        mockMvc.perform(
                        put("/api/members/%s/picture".formatted(member.getUsername()))
                                .contentType(ContentType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(profileUrlDTO2))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("회원 탈퇴 시")
    @Test
    void 회원_탈퇴() throws Exception {
        createOrLoadMember();

        mockMvc.perform(
                        delete("/api/members/testid1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("이메일 중복 체크 시")
    @Test
    void 이메일_중복_체크() throws Exception {
        createOrLoadMember();

        mockMvc.perform(
                        get(String.format("/api/members/email/%s", "email1")))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("아이디 중복 체크 시")
    @Test
    void 아이디_중복_체크() throws Exception {
        Member member = createOrLoadMember();

        mockMvc.perform(
                        get(String.format("/api/members/username/%s", member.getUsername())))
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
                        put(String.format("/api/members/%s/role-status?roleStatus=%s", "testid1", status)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("사용자 잘못된 값으로 아티스트 역할 승인 시")
    @Test
    void 아티스트_승인_잘못된값() throws Exception {
        createOrLoadMember();

        String status = "asd";

        mockMvc.perform(
                        put("/api/members/testid1/role-status")
                                .param("roleStatus", status))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("아이디 변경 시")
    @Test
    void 아이디_변경() throws Exception {
        Member loadMember = createOrLoadMember();

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember.getUsername(), "newid")))
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
                                loadMember2.getUsername())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("아이디 변경 시 유효성 검증 확인")
    @Test
    void 아이디_검증() throws Exception {
        Member loadMember1 = createOrLoadMember();

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember1.getUsername(), "1")))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember1.getUsername(), "한글")))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        put(String.format("/api/members/%s/username?newUsername=%s", loadMember1.getUsername(),
                                "asdasdasdsadasdasdasdsa")))
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
                                .param("newPassword", "newpassword123!"))
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
                                .content(objectMapper.writeValueAsString(dto)))
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

    @DisplayName("유저이름(username)으로 유저 리스트 조회")
    @Test
    void 유저_이름으로_유저_리스트_조회() throws Exception {
        Member member = createOrLoadMember(2);

        mockMvc
                .perform(get("/api/members/search/@{username}", member.getUsername()))
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

    @DisplayName("역활 별 회원 전체 조회")
    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @Test
    void 승인_대기_회원_전체_조회() throws Exception {
        createOrLoadMember(1, RoleStatus.ARTIST_PENDING);
        createOrLoadMember(2, RoleStatus.CURATOR_PENDING);
        createOrLoadMember(3, RoleStatus.CURATOR);
        createOrLoadMember(4, RoleStatus.ARTIST);
        createOrLoadMember(5, RoleStatus.ARTIST_REJECTED);
        createOrLoadMember(6, RoleStatus.CURATOR_REJECTED);
        createOrLoadMember(7);
        createOrLoadMember(8);

        mockMvc
                .perform(
                        get("/api/members/role-status")
                                .param("roleStatus", "PENDING"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("역할 상태 별 회원 전체 조회 - 잘못된 상태 전달 시")
    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @Test
    void 승인_대기_회원_전체_조회2() throws Exception {
        createOrLoadMember(1, RoleStatus.ARTIST_PENDING);
        createOrLoadMember(2, RoleStatus.CURATOR_PENDING);
        createOrLoadMember(3, RoleStatus.CURATOR);
        createOrLoadMember(4, RoleStatus.ARTIST);
        createOrLoadMember(5, RoleStatus.ARTIST_REJECTED);
        createOrLoadMember(6, RoleStatus.CURATOR_REJECTED);
        createOrLoadMember(7);
        createOrLoadMember(8);

        mockMvc
                .perform(
                        get("/api/members/role-status")
                                .param("roleStatus", "asd"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("이메일 수신 여부 변경 후 내 정보 조회")
    @WithMockCustomUser(username = "testid1", role = "USER")
    @Test
    void 이메일_수신_여부_변경() throws Exception {
        Member member = createOrLoadMember();

        mockMvc
                .perform(
                        put("/api/members/" + member.getUsername() + "/email-receive")
                                .param("emailReceive", "true"))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc
                .perform(
                        get("/api/members/" + member.getUsername()))
                .andDo(print())
                .andExpect(status().isOk());
    }

//    @DisplayName("회원 아이디 전체 조회 시")
//    @Test
//    void 회원_아이디_전체_조회() throws Exception {
//        createOrLoadMember(1);
//        createOrLoadMember(2);
//        createOrLoadMember(3);
//        createOrLoadMember(4);
//
//        mockMvc
//                .perform(
//                        get("/api/members/username"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }

    @DisplayName("회원 프로필 조회시 팀 목록 반환 성공")
    @WithMockCustomUser(username = "testid1", role = "USER")
    @Test
    void 회원_프로필_팀_조회시_팀_반환_확인() throws Exception {
        // given
        Member member = createOrLoadMember();
        Team team1 = createTeam(member, "팀이름1");
        Team team2 = createTeam(member, "팀이름2");

        // when
        String response = mockMvc.perform(
                        get("/api/auth/me")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MemberResponseDTO memberResponse = objectMapper.readValue(response, MemberResponseDTO.class);
        assertEquals(2, memberResponse.getTeams().size());
        assertEquals("팀이름1", memberResponse.getTeams().get(0).getName());
        assertEquals("팀이름2", memberResponse.getTeams().get(1).getName());
    }

    @DisplayName("사용자 프로필 조회 시 팀 반환 성공")
    @Test
    void 사용자_프로필_조회시_팀_반환_확인() throws Exception {
        // given
        Member member = createOrLoadMember();
        Team team1 = createTeam(member, "팀이름1");
        Team team2 = createTeam(member, "팀이름2");

        // when
        String response = mockMvc.perform(
                        get(String.format("/api/members/%s", member.getUsername()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MemberResponseDTO memberResponse = objectMapper.readValue(response, MemberResponseDTO.class);
        assertEquals(2, memberResponse.getTeams().size());
        assertEquals("팀이름1", memberResponse.getTeams().get(0).getName());
        assertEquals("팀이름2", memberResponse.getTeams().get(1).getName());
    }

    @DisplayName("해당 유저의 팀 목록 조회 ")
    @Test
    void 유저가_속한_팀_조회() throws Exception {
        //given
        Member member = createOrLoadMember();
        Member member2 = createOrLoadMember(2, RoleStatus.ARTIST);
        Team createTeam1 = createTeam(member, "ownerTeam1");
        Team createTeam2 = createTeam(member, "ownerTeam2");
        Team inviteTeam = createTeam(member2, "memberTeam1");
        TeamUser teamOwner = teamUserService.findByTeamIdAndUsername(inviteTeam.getId(),member2.getUsername());
        createAndInviteMember(teamOwner, member);

        //when
        String response = mockMvc.perform(get("/api/members/" + member.getUsername() + "/teams")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        //then
        MemberResponseDTO.TeamProfiles teamProfiles= objectMapper.readValue(response, MemberResponseDTO.TeamProfiles.class);
        List<MemberResponseDTO.TeamProfileWithRole> profiles = teamProfiles.getProfiles();
        assertEquals(3, profiles.size());
        assertEquals("OWNER", profiles.get(0).getRole());
        assertEquals(createTeam1.getId(), profiles.get(0).getId());
        assertEquals("OWNER", profiles.get(1).getRole());
        assertEquals(createTeam2.getId(), profiles.get(1).getId());
        assertEquals("MEMBER", profiles.get(2).getRole());
        assertEquals(inviteTeam.getId(), profiles.get(2).getId());
    }
}
