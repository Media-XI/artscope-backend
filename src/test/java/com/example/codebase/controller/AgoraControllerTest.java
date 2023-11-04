package com.example.codebase.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.agora.dto.AgoraCreateDTO;
import com.example.codebase.domain.agora.dto.AgoraMediaCreateDTO;
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
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private S3Service s3Service;

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
        return createOrLoadMember("testid", "ROLE_CURATOR");
    }

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

        Member save = memberRepository.save(dummy);
        return save;
    }

    private byte[] createImageFile() throws IOException {
        File file = resourceLoader.getResource("classpath:test/img.jpg").getFile(); // TODO : 테스트용 이미지 파일
        return Files.readAllBytes(file.toPath());
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
}