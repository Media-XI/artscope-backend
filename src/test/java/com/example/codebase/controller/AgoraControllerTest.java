package com.example.codebase.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.agora.dto.AgoraCreateDTO;
import com.example.codebase.domain.agora.dto.AgoraMediaCreateDTO;
import com.example.codebase.domain.agora.dto.AgoraUpdateDTO;
import com.example.codebase.domain.agora.entity.*;
import com.example.codebase.domain.agora.repository.AgoraOpinionRepository;
import com.example.codebase.domain.agora.repository.AgoraParticipantRepository;
import com.example.codebase.domain.agora.repository.AgoraRepository;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.s3.S3Service;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(S3MockConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class AgoraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private AgoraRepository agoraRepository;

    @Autowired
    private AgoraParticipantRepository agoraParticipantRepository;

    @Autowired
    private AgoraOpinionRepository agoraOpinionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private S3Service s3Service;

    @PersistenceContext
    private EntityManager entityManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeAll
    static void setUp(@Autowired S3Mock s3Mock,
                      @Autowired AmazonS3 amazonS3) {
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
        return createOrLoadMember("testid", "ROLE_USER");
    }

    @Transactional
    public Member createOrLoadMember(String username, String... authorities) {
        Optional<Member> testMember = memberRepository.findByUsername(username);
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy =
                Member.builder()
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
            dummy.addAuthority(memberAuthority);
        }

        return memberRepository.save(dummy);
    }

    public Agora createOrLoadAgora(boolean isAnnoymous) throws IOException {
        return createOrLoadAgora(1, 1, isAnnoymous);
    }

    public String uploadFileToS3() throws IOException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
                createImageFile());
        return s3Service.saveUploadFile(mockMultipartFile);
    }

    @Transactional
    public Agora createOrLoadAgora(int index, int mediaSize, boolean isAnonymous) throws IOException {
        AgoraCreateDTO dto = new AgoraCreateDTO();
        dto.setTitle("AI 생성형 이미지 어떻게 생각하십니까");
        dto.setContent("생성형 이미지를 찬성하는지?");
        dto.setAgreeText("찬성");
        dto.setNaturalText("중립");
        dto.setDisagreeText("반대");
        dto.setIsAnonymous(isAnonymous);
        dto.setMedias(Collections.singletonList(AgoraMediaCreateDTO.builder()
                .mediaType("image")
                .mediaUrl("test.jpg")
                .build()));
        dto.setThumbnail(AgoraMediaCreateDTO.builder()
                .mediaType("image")
                .mediaUrl("test.jpg")
                .build());

        Member member = createOrLoadMember();

        Agora agora = Agora.of(dto, member);

        // 썸네일 추가\
        dto.getThumbnail().setMediaUrl(uploadFileToS3());

        AgoraMedia thumbnail = AgoraMedia.from(dto.getThumbnail());
        thumbnail.setAgora(agora);

        // 미디어 추가
        for (AgoraMediaCreateDTO mediaCreateDTO : dto.getMedias()) {
            mediaCreateDTO.setMediaUrl(uploadFileToS3());

            AgoraMedia agoraMedia = AgoraMedia.from(mediaCreateDTO);
            agoraMedia.setAgora(agora);
        }
        agoraRepository.save(agora);

        // 작성자를 아고라 참여자로 추가
        AgoraParticipant participant = AgoraParticipant.create();
        participant.setAgoraAndMember(agora, member);
        agoraParticipantRepository.save(participant);

        return agora;
    }

    @Transactional
    public AgoraParticipant createOrLoadAgoraParticipant(Agora agora, Member member, String vote) {
        AgoraParticipant participant = agoraParticipantRepository.findById(AgoraParticipantIds.of(agora, member))
                .orElse(null);

        if (participant != null) {
            return participant;
        }

        participant = AgoraParticipant.create();
        participant.setAgoraAndMember(agora, member);
        participant.newSequence();
        participant.newVote(vote);
        return agoraParticipantRepository.save(participant);
    }

    @Transactional
    public AgoraOpinion createAgoraOpinion(Agora agora, AgoraParticipant agoraParticipant, String content) {
        AgoraOpinion opinion = AgoraOpinion.from(content);
        opinion.setAgoraAndAuthor(agora, agoraParticipant);
        return agoraOpinionRepository.save(opinion);
    }


    private byte[] createImageFile() throws IOException {
        File file = resourceLoader.getResource("classpath:test/img.jpg").getFile(); // TODO : 테스트용 이미지 파일
        return Files.readAllBytes(file.toPath());
    }

    @DisplayName("아고라 전체 조회")
    @Test
    public void 아고라_전체조회 () throws Exception {
        createOrLoadAgora(1, 1, true);
        createOrLoadAgora(2, 1, false);
        createOrLoadAgora(3, 1, false);
        createOrLoadAgora(4, 1, false);
        createOrLoadAgora(5, 1, true);

        mockMvc.perform(
                get("/api/agoras")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("아고라 익명으로 생성")
    @Test
    public void 아고라_생성1() throws Exception {
        createOrLoadMember("admin", "ROLE_ADMIN");

        AgoraMediaCreateDTO thumbnailCreateDTO = new AgoraMediaCreateDTO();
        thumbnailCreateDTO.setMediaType("image");

        AgoraMediaCreateDTO mediaCreateDTO = new AgoraMediaCreateDTO();
        mediaCreateDTO.setMediaType("image");

        AgoraCreateDTO agoraCreateDTO = new AgoraCreateDTO();
        agoraCreateDTO.setTitle("AI 생성형 이미지 어떻게 생각하십니까");
        agoraCreateDTO.setContent("생성형 이미지를 찬성하는지?");
        agoraCreateDTO.setAgreeText("AI 찬성");
        agoraCreateDTO.setNaturalText("AI 중립");
        agoraCreateDTO.setDisagreeText("AI 반대");
        agoraCreateDTO.setIsAnonymous(true);
        agoraCreateDTO.setThumbnail(thumbnailCreateDTO);
        agoraCreateDTO.setMedias(Collections.singletonList(mediaCreateDTO));

        MockMultipartFile dto = new MockMultipartFile("dto", "", "application/json",
                objectMapper.writeValueAsBytes(agoraCreateDTO));

        List<MockMultipartFile> mediaFiles = new ArrayList<>();

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
                createImageFile());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
                createImageFile());
        mediaFiles.add(mockMultipartFile);

        mockMvc.perform(
                        multipart("/api/agoras")
                                .file(thumbnailFile)
                                .file(mockMultipartFile)
                                .file(dto)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("아고라 실명으로 생성")
    @Test
    public void 아고라_생성2() throws Exception {
        createOrLoadMember("admin", "ROLE_ADMIN");

        AgoraMediaCreateDTO thumbnailCreateDTO = new AgoraMediaCreateDTO();
        thumbnailCreateDTO.setMediaType("image");

        AgoraMediaCreateDTO mediaCreateDTO = new AgoraMediaCreateDTO();
        mediaCreateDTO.setMediaType("image");

        AgoraCreateDTO agoraCreateDTO = new AgoraCreateDTO();
        agoraCreateDTO.setTitle("AI 생성형 이미지 어떻게 생각하십니까");
        agoraCreateDTO.setContent("생성형 이미지를 찬성하는지? 실명으로 얘기해봐요 ㅋㅋ");
        agoraCreateDTO.setAgreeText("AI 찬성");
        agoraCreateDTO.setNaturalText("AI 중립");
        agoraCreateDTO.setDisagreeText("AI 반대");
        agoraCreateDTO.setIsAnonymous(false);
        agoraCreateDTO.setThumbnail(thumbnailCreateDTO);
        agoraCreateDTO.setMedias(Collections.singletonList(mediaCreateDTO));

        MockMultipartFile dto = new MockMultipartFile("dto", "", "application/json",
                objectMapper.writeValueAsBytes(agoraCreateDTO));

        List<MockMultipartFile> mediaFiles = new ArrayList<>();

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
                createImageFile());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
                createImageFile());
        mediaFiles.add(mockMultipartFile);

        mockMvc.perform(
                        multipart("/api/agoras")
                                .file(thumbnailFile)
                                .file(mockMultipartFile)
                                .file(dto)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("익명 아고라 상세 조회")
    @Test
    public void 아고라_상세조회1() throws Exception {
        Agora agora = createOrLoadAgora(true);

        mockMvc.perform(
                        get("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("실명 아고라 상세 조회")
    @Test
    public void 아고라_상세조회2() throws Exception {
        Agora agora = createOrLoadAgora(false);

        mockMvc.perform(
                        get("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아고라 수정")
    @Test
    public void 아고라_수정1() throws Exception {
        Agora agora = createOrLoadAgora(true);

        AgoraUpdateDTO updateDTO = new AgoraUpdateDTO();
        updateDTO.setTitle("AI 생성형 이미지 어떻게 생각하십니까");
        updateDTO.setContent("수정");
        updateDTO.setAgreeText("AI 찬성");
        updateDTO.setDisagreeText("AI 반대");
        updateDTO.setIsAnonymous(false);

        mockMvc.perform(
                        put("/api/agoras/" + agora.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("작성자가 아고라 삭제")
    @Test
    public void 아고라_삭제1() throws Exception {
        Agora agora = createOrLoadAgora(true);

        mockMvc.perform(
                        delete("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "testid2", role = "USER")
    @DisplayName("아고라 투표")
    @Test
    public void 아고라_투표() throws Exception {
        createOrLoadMember("testid2", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("찬성")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid2", role = "USER")
    @DisplayName("아고라 중립 투표 시")
    @Test
    public void 아고라_중립_투표() throws Exception {
        createOrLoadMember("testid2", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("중립")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아고라 작성자가 투표 시")
    @Test
    public void 아고라_작성자_투표() throws Exception {
        Agora agora = createOrLoadAgora(true);

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("중립")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


    @WithMockCustomUser(username = "testid2", role = "USER")
    @DisplayName("아고라 투표 및 투표 변경")
    @Test
    public void 아고라_투표2() throws Exception {
        createOrLoadMember("testid2", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("찬성")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("반대")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid2", role = "USER")
    @DisplayName("아고라 투표 및 투표 취소")
    @Test
    public void 아고라_투표3() throws Exception {
        createOrLoadMember("testid2", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("찬성")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("찬성")
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "testid2", role = "USER")
    @DisplayName("아고라 투표 취소 이후 재투표")
    @Test
    public void 아고라_투표4() throws Exception {
        createOrLoadMember("testid2", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("찬성")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("찬성")
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("반대")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("익명 아고라 의견 생성")
    @Test
    public void 아고라_의견_생성 () throws Exception {
        Member loginMember = createOrLoadMember("user", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        createOrLoadAgoraParticipant(agora, loginMember, "찬성");

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/opinions")
                                .content("의견")
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("익명 아고라 작성자가 의견 생성 시")
    @Test
    public void 아고라_의견_작성자가_생성 () throws Exception {

        Agora agora = createOrLoadAgora(true);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/vote")
                                .content("찬성")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/opinions")
                                .content("작성자 의견입니다.")
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("실명 아고라 의견 생성")
    @Test
    public void 아고라_의견_생성2 () throws Exception {
        Member loginMember = createOrLoadMember("user", "ROLE_USER");

        Agora agora = createOrLoadAgora(false);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        createOrLoadAgoraParticipant(agora, loginMember, "찬성");

        mockMvc.perform(
                        post("/api/agoras/" + agora.getId() + "/opinions")
                                .content("의견")
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("아고라 의견 수정")
    @Test
    public void 아고라_의견_수정 () throws Exception {
        Member loginMember = createOrLoadMember("user", "ROLE_USER");

        Agora agora = createOrLoadAgora(false);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant = createOrLoadAgoraParticipant(agora, loginMember, "찬성");

        // 의견 생성
        AgoraOpinion opinion = createAgoraOpinion(agora, participant, "찬성하는 의견입니다 어쩌구");

        mockMvc.perform(
                        put("/api/agoras/" + agora.getId() + "/opinions/" + opinion.getId())
                                .content("의견 바꿀게요")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user12", role = "USER")
    @DisplayName("작성자가 아닌데 아고라 의견 수정 시")
    @Test
    public void 아고라_의견_수정1 () throws Exception {
        Member loginMember = createOrLoadMember("user", "ROLE_USER");

        Agora agora = createOrLoadAgora(false);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant = createOrLoadAgoraParticipant(agora, loginMember, "찬성");

        // 의견 생성
        AgoraOpinion opinion = createAgoraOpinion(agora, participant, "찬성하는 의견입니다 어쩌구");

        mockMvc.perform(
                        put("/api/agoras/" + agora.getId() + "/opinions/" + opinion.getId())
                                .content("의견 바꿀게요")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user12", role = "USER")
    @DisplayName("다른 아고라 ID를 이용해 아고라 의견 수정 시")
    @Test
    public void 아고라_의견_수정2 () throws Exception {
        Member loginMember = createOrLoadMember("user", "ROLE_USER");

        Agora agora = createOrLoadAgora(false);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant = createOrLoadAgoraParticipant(agora, loginMember, "찬성");

        // 의견 생성
        AgoraOpinion opinion = createAgoraOpinion(agora, participant, "찬성하는 의견입니다 어쩌구");

        mockMvc.perform(
                        put("/api/agoras/" + 0 + "/opinions/" + opinion.getId())
                                .content("의견 바꿀게요")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("아고라 삭제 시")
    @Test
    public void 아고라_의견_삭제 () throws Exception {
        Member loginMember = createOrLoadMember("user", "ROLE_USER");

        Agora agora = createOrLoadAgora(false);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant = createOrLoadAgoraParticipant(agora, loginMember, "찬성");

        // 의견 생성
        AgoraOpinion opinion = createAgoraOpinion(agora, participant, "찬성하는 의견입니다 어쩌구");

        mockMvc.perform(
                        delete("/api/agoras/" + agora.getId() + "/opinions/" + opinion.getId())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "user123", role = "USER")
    @DisplayName("작성자가 아닌데 아고라 삭제 시")
    @Test
    public void 아고라_의견_삭제2() throws Exception {
        Member loginMember = createOrLoadMember("user", "ROLE_USER");

        Agora agora = createOrLoadAgora(false);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant = createOrLoadAgoraParticipant(agora, loginMember, "찬성");

        // 의견 생성
        AgoraOpinion opinion = createAgoraOpinion(agora, participant, "찬성하는 의견입니다 어쩌구");

        mockMvc.perform(
                        delete("/api/agoras/" + agora.getId() + "/opinions/" + opinion.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("어드민이 아고라 삭제 시")
    @Test
    public void 아고라_의견_삭제3 () throws Exception {
        Member loginMember = createOrLoadMember("user", "ROLE_USER");

        Agora agora = createOrLoadAgora(false);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant = createOrLoadAgoraParticipant(agora, loginMember, "찬성");

        // 의견 생성
        AgoraOpinion opinion = createAgoraOpinion(agora, participant, "찬성하는 의견입니다 어쩌구");

        mockMvc.perform(
                        delete("/api/agoras/" + agora.getId() + "/opinions/" + opinion.getId())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("의견이 여러개 있는 실명 아고라 조회 시")
    @Test
    public void 의견이_있는_아고라_조회 () throws Exception {
        Member member1 = createOrLoadMember("user1", "ROLE_USER");
        Member member2 = createOrLoadMember("user2", "ROLE_USER");
        Member member3 = createOrLoadMember("user3", "ROLE_USER");
        Member member4 = createOrLoadMember("user4", "ROLE_USER");

        Agora agora = createOrLoadAgora(false);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant1 = createOrLoadAgoraParticipant(agora, member1, "찬성");
        AgoraParticipant participant2 = createOrLoadAgoraParticipant(agora, member2, "중립");
        AgoraParticipant participant3 = createOrLoadAgoraParticipant(agora, member3, "반대");
        AgoraParticipant participant4 = createOrLoadAgoraParticipant(agora, member4, "반대");

        // 의견 생성
        createAgoraOpinion(agora, participant1, "찬성하는 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant2, "중립하는 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant3, "반대하는 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant4, "반대하는 의견입니다 어쩌구");

        mockMvc.perform(
                        get("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("의견이 여러개 있는 익명 아고라 조회 시")
    @Test
    public void 의견이_있는_아고라_조회2 () throws Exception {
        Member member1 = createOrLoadMember("user1", "ROLE_USER");
        Member member2 = createOrLoadMember("user2", "ROLE_USER");
        Member member3 = createOrLoadMember("user3", "ROLE_USER");
        Member member4 = createOrLoadMember("user4", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant1 = createOrLoadAgoraParticipant(agora, member1, "찬성");
        AgoraParticipant participant2 = createOrLoadAgoraParticipant(agora, member2, "중립");
        AgoraParticipant participant3 = createOrLoadAgoraParticipant(agora, member3, "반대");
        AgoraParticipant participant4 = createOrLoadAgoraParticipant(agora, member4, "반대");

        // 의견 생성
        createAgoraOpinion(agora, participant1, "찬성하는 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant1, "다른 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant2, "중립하는 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant3, "반대하는 의견입니다 어쩌구");

        mockMvc.perform(
                        get("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("의견이 여러개 있는 아고라 삭제 및 조회 시")
    @Test
    public void 의견이_있는_아고라_삭제1 () throws Exception {
        Member member1 = createOrLoadMember("user1", "ROLE_USER");
        Member member2 = createOrLoadMember("user2", "ROLE_USER");
        Member member3 = createOrLoadMember("user3", "ROLE_USER");
        Member member4 = createOrLoadMember("user4", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        AgoraParticipant participant1 = createOrLoadAgoraParticipant(agora, member1, "찬성");
        AgoraParticipant participant2 = createOrLoadAgoraParticipant(agora, member2, "중립");
        AgoraParticipant participant3 = createOrLoadAgoraParticipant(agora, member3, "반대");
        AgoraParticipant participant4 = createOrLoadAgoraParticipant(agora, member4, "반대");

        // 의견 생성
        createAgoraOpinion(agora, participant1, "찬성하는 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant1, "다른 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant2, "중립하는 의견입니다 어쩌구");
        createAgoraOpinion(agora, participant3, "반대하는 의견입니다 어쩌구");

        mockMvc.perform(
                        delete("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest()); // 삭제 실패

        mockMvc.perform(
                        get("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("의견이 없고 참가자가 여러 있는 아고라 삭제 및 조회 시")
    @Test
    public void 참가자가_있는_아고라_삭제1 () throws Exception {
        Member member1 = createOrLoadMember("user1", "ROLE_USER");
        Member member2 = createOrLoadMember("user2", "ROLE_USER");
        Member member3 = createOrLoadMember("user3", "ROLE_USER");
        Member member4 = createOrLoadMember("user4", "ROLE_USER");

        Agora agora = createOrLoadAgora(true);

        // 의견을 달기 위해선 미리 투표가 되어야한다.
        createOrLoadAgoraParticipant(agora, member1, "찬성");
        createOrLoadAgoraParticipant(agora, member2, "중립");
        createOrLoadAgoraParticipant(agora, member3, "반대");
        createOrLoadAgoraParticipant(agora, member4, "반대");

        mockMvc.perform(
                        delete("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/api/agoras/" + agora.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}