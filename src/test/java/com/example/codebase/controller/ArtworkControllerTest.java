package com.example.codebase.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.artwork.dto.ArtworkCommentCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkUpdateDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkComment;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
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
class ArtworkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private S3Service s3Service;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
        objectMapper.registerModule(new JavaTimeModule());
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

        Member save = memberRepository.save(dummy);
        return save;
    }

    public Artwork createOrLoadArtwork() throws IOException {
        return createOrLoadArtwork(1, true, 1);
    }

    public Artwork createOrLoadArtwork(int index, boolean isVisible) throws IOException {
        return createOrLoadArtwork(index, isVisible, 1);
    }

    @Transactional
    public Artwork createOrLoadArtwork(int index, boolean isVisible, int mediaSize) throws IOException {
        Optional<Artwork> artwork = artworkRepository.findById(Long.valueOf(index));
        if (artwork.isPresent()) {
            return artwork.get();
        }

        List<ArtworkMedia> artworkMediaList = new ArrayList<>();
        for (int i = 0; i < mediaSize; i++) {
            MockMultipartFile mockMultipartFile = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
                createImageFile());
            String url = s3Service.saveUploadFile(mockMultipartFile);

            ArtworkMedia artworkMedia = ArtworkMedia.builder()
                .artworkMediaType(com.example.codebase.domain.media.MediaType.image)
                .mediaUrl(url)
                .description("미디어 설명")
                .build();
            artworkMediaList.add(artworkMedia);
        }

        Artwork dummy = Artwork.builder()
            .title("아트워크_테스트" + index)
            .description("작품 설명")
            .tags("태그1,태그2,태그3")
            .visible(isVisible)
            .member(createOrLoadMember())
            .artworkMedia(artworkMediaList)
            .createdTime(LocalDateTime.now().plusSeconds(index))
            .build();

        return artworkRepository.save(dummy);
    }

    @Transactional
    public Artwork createArtworkWithComment(int commentSize) throws IOException {
        Artwork artwork = createOrLoadArtwork();

        for (int i = 0; i < commentSize; i++) {
            ArtworkComment artworkComment = ArtworkComment.builder()
                .artwork(artwork)
                .author(createOrLoadMember())
                .content("댓글 내용" + i)
                .createdTime(LocalDateTime.now().plusMinutes(i))
                .build();
            artwork.addArtworkComment(artworkComment);
        }
        artworkRepository.save(artwork);
        return artwork;
    }

    private byte[] createImageFile() throws IOException {
        File file = resourceLoader.getResource("classpath:test/img.jpg").getFile(); // TODO : 테스트용 이미지 파일
        return Files.readAllBytes(file.toPath());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 등록")
    @Test
    public void test00() throws Exception {
        createOrLoadMember();

        ArtworkMediaCreateDTO thumbnailCreateDTO = new ArtworkMediaCreateDTO();
        thumbnailCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        thumbnailCreateDTO.setDescription("썸네일 설명");

        ArtworkMediaCreateDTO mediaCreateDTO = new ArtworkMediaCreateDTO();
        mediaCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        mediaCreateDTO.setDescription("미디어 설명");

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setTags(Arrays.asList("태그1", "태그2", "태그3"));
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setThumbnail(thumbnailCreateDTO);
        dto.setMedias(Collections.singletonList(mediaCreateDTO));

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
            createImageFile());

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
            createImageFile());
        mediaFiles.add(mockMultipartFile);

        mockMvc.perform(
                multipart("/api/artworks")
                    .file(mediaFiles.get(0))
                    .file(thumbnailFile)
                    .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")

            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 URL 미디어 등록")
    @Test
    public void 아트워크_URL_등록() throws Exception {
        createOrLoadMember();

        ArtworkMediaCreateDTO thumbnailCreateDTO = new ArtworkMediaCreateDTO();
        thumbnailCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        thumbnailCreateDTO.setDescription("썸네일 설명");

        List<ArtworkMediaCreateDTO> mediaCreateDTOList = new ArrayList<>();
        ArtworkMediaCreateDTO mediaCreateDTO1 = new ArtworkMediaCreateDTO();
        mediaCreateDTO1.setMediaType(com.example.codebase.domain.media.MediaType.url.toString());
        mediaCreateDTO1.setDescription("url 미디어 설명");
        mediaCreateDTOList.add(mediaCreateDTO1);

        ArtworkMediaCreateDTO mediaCreateDTO2 = new ArtworkMediaCreateDTO();
        mediaCreateDTO2.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        mediaCreateDTO2.setDescription("미디어 설명2");
        mediaCreateDTOList.add(mediaCreateDTO2);

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setTags(Arrays.asList("태그1", "태그2", "태그3"));
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setThumbnail(thumbnailCreateDTO);
        dto.setMedias(mediaCreateDTOList);

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
            createImageFile());

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("mediaFiles", "url", "text/plane",
            "https://www.youtube.com/watch?v=95YLHDzsg8A".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
            createImageFile());
        mediaFiles.add(mockMultipartFile1);
        mediaFiles.add(mockMultipartFile2);

        mockMvc.perform(
                multipart("/api/artworks")
                    .file(mediaFiles.get(0))
                    .file(mediaFiles.get(1))
                    .file(thumbnailFile)
                    .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")

            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 URL 미디어 등록 (filename 확장자가 jpg일때)")
    @Test
    public void 아트워크_URL_등록_확장자가_이미지() throws Exception {
        createOrLoadMember();

        ArtworkMediaCreateDTO thumbnailCreateDTO = new ArtworkMediaCreateDTO();
        thumbnailCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        thumbnailCreateDTO.setDescription("썸네일 설명");

        List<ArtworkMediaCreateDTO> mediaCreateDTOList = new ArrayList<>();
        ArtworkMediaCreateDTO mediaCreateDTO1 = new ArtworkMediaCreateDTO();
        mediaCreateDTO1.setMediaType(com.example.codebase.domain.media.MediaType.url.toString());
        mediaCreateDTO1.setDescription("url 미디어 설명");
        mediaCreateDTOList.add(mediaCreateDTO1);

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setTags(Arrays.asList("태그1", "태그2", "태그3"));
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setThumbnail(thumbnailCreateDTO);
        dto.setMedias(mediaCreateDTOList);

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
            createImageFile());

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("mediaFiles", "image.jpg", "text/plain",
            "https://youtu.be/95YLHDzsg8A?t=153".getBytes());
        mediaFiles.add(mockMultipartFile1);

        mockMvc.perform(
                multipart("/api/artworks")
                    .file(mediaFiles.get(0))
                    .file(thumbnailFile)
                    .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")

            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 URL 유튜브 주소가 아닐 시")
    @Test
    public void 아트워크_URL_등록_유튜브아닐시() throws Exception {
        createOrLoadMember();

        ArtworkMediaCreateDTO thumbnailCreateDTO = new ArtworkMediaCreateDTO();
        thumbnailCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        thumbnailCreateDTO.setDescription("썸네일 설명");

        List<ArtworkMediaCreateDTO> mediaCreateDTOList = new ArrayList<>();
        ArtworkMediaCreateDTO mediaCreateDTO1 = new ArtworkMediaCreateDTO();
        mediaCreateDTO1.setMediaType(com.example.codebase.domain.media.MediaType.url.toString());
        mediaCreateDTO1.setDescription("url 미디어 설명");
        mediaCreateDTOList.add(mediaCreateDTO1);

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setTags(Arrays.asList("태그1", "태그2", "태그3"));
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setThumbnail(thumbnailCreateDTO);
        dto.setMedias(mediaCreateDTOList);

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
            createImageFile());

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("mediaFiles", "url", "text/plane",
            "https://www.youtube.com".getBytes());
        mediaFiles.add(mockMultipartFile1);

        mockMvc.perform(
                multipart("/api/artworks")
                    .file(mediaFiles.get(0))
                    .file(thumbnailFile)
                    .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크에 미디어 두개 등록")
    @Test
    public void test01() throws Exception {
        createOrLoadMember();

        List<ArtworkMediaCreateDTO> mediaCreateDTOList = new ArrayList<>();

        ArtworkMediaCreateDTO thumbnailCreateDTO = new ArtworkMediaCreateDTO();
        thumbnailCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        thumbnailCreateDTO.setDescription("썸네일 설명");

        ArtworkMediaCreateDTO mediaCreateDTO1 = new ArtworkMediaCreateDTO();
        mediaCreateDTO1.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        mediaCreateDTO1.setDescription("미디어 설명1");
        mediaCreateDTOList.add(mediaCreateDTO1);
        ArtworkMediaCreateDTO mediaCreateDTO2 = new ArtworkMediaCreateDTO();
        mediaCreateDTO2.setMediaType(com.example.codebase.domain.media.MediaType.video.toString());
        mediaCreateDTO2.setDescription("미디어 설명2");
        mediaCreateDTOList.add(mediaCreateDTO2);

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
            createImageFile());

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("mediaFiles", "image1.jpg", "image/jpg",
            createImageFile());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("mediaFiles", "image2.jpg", "image/jpg",
            createImageFile());

        mediaFiles.add(mockMultipartFile1);
        mediaFiles.add(mockMultipartFile2);

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setThumbnail(thumbnailCreateDTO);
        dto.setMedias(mediaCreateDTOList);

        mockMvc.perform(
                multipart("/api/artworks")
                    .file(mediaFiles.get(0))
                    .file(mediaFiles.get(1))
                    .file(thumbnailFile)
                    .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @DisplayName("아트워크 4개 전체 조회")
    @Test
    public void test02() throws Exception {
        createOrLoadArtwork(10, true, 1);
        createOrLoadArtwork(11, false, 1);
        createOrLoadArtwork(12, true, 1);
        createOrLoadArtwork(13, true, 1);

        mockMvc.perform(
                get("/api/artworks?page=0&size=10")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("아트워크 상세 조회")
    @Test
    public void test03() throws Exception {
        Artwork artwork = createOrLoadArtwork();

        mockMvc.perform(
                get(String.format("/api/artworks/%d", artwork.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 미디어 부적절한 타입일 시 테스트")
    @Test
    public void test04() throws Exception {
        createOrLoadMember();

        ArtworkMediaCreateDTO thumbnailCreateDTO = new ArtworkMediaCreateDTO();
        thumbnailCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        thumbnailCreateDTO.setDescription("썸네일 설명");

        ArtworkMediaCreateDTO mediaCreateDTO = new ArtworkMediaCreateDTO();
        mediaCreateDTO.setMediaType("test");
        mediaCreateDTO.setMediaUrl("url");
        mediaCreateDTO.setDescription("미디어 설명");

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        mediaFiles.add(new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", "test".getBytes()));

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
            createImageFile());

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(false);
        dto.setThumbnail(thumbnailCreateDTO);
        dto.setMedias(Collections.singletonList(mediaCreateDTO));

        mockMvc.perform(
                multipart("/api/artworks")
                    .file(mediaFiles.get(0))
                    .file(thumbnailFile)
                    .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
            )
            .andDo(print())
            .andExpect(status().isInternalServerError());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("일반 사용자 아트워크 수정")
    @Test
    public void test05() throws Exception {
        Artwork artwork = createOrLoadArtwork();

        ArtworkUpdateDTO dto = new ArtworkUpdateDTO();
        dto.setTitle("아트워크_수정");
        dto.setDescription("작품 수정함 설명");
        dto.setVisible(true);

        mockMvc.perform(
                put(String.format("/api/artworks/%d", artwork.getId()))
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("작성자가 아닌 회원이 아트워크 수정 시")
    @Test
    public void test06() throws Exception {
        Artwork artwork = createOrLoadArtwork();

        ArtworkUpdateDTO dto = new ArtworkUpdateDTO();
        dto.setTitle("아트워크_수정");
        dto.setDescription("작품 수정함 설명");
        dto.setVisible(true);

        mockMvc.perform(
                put(String.format("/api/artworks/%d", artwork.getId()))
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user", role = "ADMIN")
    @DisplayName("관리자가 아트워크 수정 시")
    @Test
    public void 관리자_아트워크_수정() throws Exception {
        createOrLoadMember("user", "ROLE_ADMIN", "ROLE_USER");
        Artwork artwork = createOrLoadArtwork();

        ArtworkUpdateDTO dto = new ArtworkUpdateDTO();
        dto.setTitle("아트워크_수정");
        dto.setDescription("작품 수정함 설명");
        dto.setVisible(true);

        mockMvc.perform(
                put(String.format("/api/artworks/%d", artwork.getId()))
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 미디어 수정 시")
    @Test
    public void test07() throws Exception {
        Artwork artwork = createOrLoadArtwork();

        ArtworkMediaCreateDTO dto = new ArtworkMediaCreateDTO();
        dto.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        dto.setMediaUrl("수정한 URL");
        dto.setDescription("수정한 설명");

        mockMvc.perform(
                put(String.format("/api/artworks/%d/media/%d", artwork.getId(),
                    artwork.getArtworkMedia().get(0).getId()))
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("미디어 파일 없는 아트워크 등록 시 ")
    @Test
    public void 아트워크_등록_시_미디어_파일_부재() throws Exception {
        createOrLoadMember();

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(true);

        mockMvc.perform(
                multipart("/api/artworks")
                    .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 오디오 등록 시")
    @Test
    public void 아트워크_오디오_등록() throws Exception {
        createOrLoadMember();

        ArtworkMediaCreateDTO thumbnailCreateDTO = new ArtworkMediaCreateDTO();
        thumbnailCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.image.toString());
        thumbnailCreateDTO.setDescription("썸네일 설명");

        ArtworkMediaCreateDTO mediaCreateDTO = new ArtworkMediaCreateDTO();
        mediaCreateDTO.setMediaType(com.example.codebase.domain.media.MediaType.audio.toString());
        mediaCreateDTO.setDescription("미디어 설명");

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setMedias(Collections.singletonList(mediaCreateDTO));
        dto.setThumbnail(thumbnailCreateDTO);

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        MockMultipartFile mockMultipartFile = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
            createImageFile());
        mediaFiles.add(mockMultipartFile);

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
            createImageFile());

        mockMvc.perform(
                multipart("/api/artworks")
                    .file(mediaFiles.get(0))
                    .file(thumbnailFile)
                    .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")

            )
            .andDo(print())
            .andExpect(status().isCreated());
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 삭제 시")
    @Test
    public void 일반사용자_아트워크_삭제() throws Exception {
        Artwork artwork = createOrLoadArtwork();

        mockMvc.perform(
                delete(String.format("/api/artworks/%d", artwork.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("작성자가 아닌데 아트워크 삭제 시")
    @Test
    public void 작성자가아닌데_아트워크_삭제() throws Exception {
        createOrLoadMember("user", "ROLE_USER");

        Artwork artwork = createOrLoadArtwork();

        mockMvc.perform(
                delete(String.format("/api/artworks/%d", artwork.getId()))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("일반 사용자 아트워크 미디어 여러개있을때 삭제")
    @Test
    public void 일반사용자_아트워크_미디어여러개_삭제() throws Exception {
        Artwork artwork = createOrLoadArtwork(1, false, 3);

        mockMvc.perform(
                delete(String.format("/api/artworks/%d", artwork.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }


    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("관리자 아트워크 삭제")
    @Test
    public void 관리자_아트워크_삭제() throws Exception {
        Artwork artwork = createOrLoadArtwork();

        mockMvc.perform(
                delete(String.format("/api/artworks/%d", artwork.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("해당 사용자 아트워크 오래된 순으로 조회")
    @Test
    public void 사용자_아트워크_조회() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, true);
        String username = artwork1.getMember().getUsername();

        mockMvc.perform(
                get(String.format("/api/artworks/member/%s?size=10&page=0&sortDirection=ASC", username))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("해당 아트워크 좋아요 API 두번 호출 시 ")
    @Test
    public void 아트워크_좋아요_호출() throws Exception {
        Artwork artwork = createOrLoadArtwork();

        // 좋아요 발생
        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        // 좋아요 취소
        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork.getId()))
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("해당 사용자의 좋아요 여부와 함께 아트워크 전체 조회 시")
    @Test
    public void 사용자_좋아요_아트워크_전체_조회() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, true);
        String username = artwork1.getMember().getUsername();

        // 좋아요 발생
        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork2.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(
                get(String.format("/api/artworks/member/%s/likes", username))
            )
            .andDo(print())
            .andExpect(status().isOk());

    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("로그인한 사용자의 좋아요 여부와 함께 아트워크 전체 조회 시")
    @Test
    public void 로그인사용자_좋아요_아트워크_전체_조회() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, true);
        Artwork artwork3 = createOrLoadArtwork(3, true);

        String username = artwork1.getMember().getUsername();

        // 좋아요 발생
        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork2.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        // 로그인한 사용자의 좋아요 여부와 함께 아트워크 전체 조회
        mockMvc.perform(
                get(String.format("/api/artworks/likes", username))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 좋아요한 사용자들 조회 시")
    @Test
    public void 해당_아트워크_좋아요_전체_조회() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        String username = artwork1.getMember().getUsername();

        // 좋아요 발생
        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        // 좋아요 표시한 사용자들 조회
        mockMvc.perform(
                get(String.format("/api/artworks/%d/likes", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("전체 아트워크 조회 시 좋아요 여부와 함께 조회")
    @Test
    public void 좋아요_여부_와_전체_아트워크() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, true);

        String username = artwork1.getMember().getUsername();

        // 좋아요 발생
        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        // 좋아요 표시한 사용자들 조회
        mockMvc.perform(
                get("/api/artworks")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("비회원이 전체 아트워크 조회 시 좋아요 여부와 함께 조회")
    @Test
    public void 비회원_좋아요_여부_와_전체_아트워크() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, true);

        String username = artwork1.getMember().getUsername();

        // 좋아요 표시한 사용자들 조회
        mockMvc.perform(
                get("/api/artworks")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("단일 아트워크 조회 시 좋아요 여부와 함께 조회")
    @Test
    public void 좋아요_여부_와_단일_아트워크() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, true);

        String username = artwork1.getMember().getUsername();

        // 좋아요 발생
        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        // 좋아요 표시한 아트워크
        mockMvc.perform(
                get(String.format("/api/artworks/%d", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        // 좋아요 안한 표시한 아트워크
        mockMvc.perform(
                get(String.format("/api/artworks/%d", artwork2.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("특정 아트워크 조회 시 로그인한 사용자의 좋아요 여부와 함께 조회")
    @Test
    public void 로그인사용자의_좋아요_여부_와_특정_아트워크() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, true);

        String username = artwork1.getMember().getUsername();

        // 좋아요 발생
        mockMvc.perform(
                post(String.format("/api/artworks/%d/like", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        // 특정 아트워크 좋아요 여부
        mockMvc.perform(
                get(String.format("/api/artworks/%d/member/likes", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(
                get(String.format("/api/artworks/%d/member/likes", artwork2.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());

    }

    @DisplayName("비회원이 단일 아트워크 조회 시 좋아요 여부와 함께 조회")
    @Test
    public void 비회원_좋아요_여부_와_단일_아트워크() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);

        String username = artwork1.getMember().getUsername();

        // 좋아요 표시한 사용자들 조회
        mockMvc.perform(
                get(String.format("/api/artworks/%d", artwork1.getId()))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("아트워크 작품명, 태그명, 작가명으로 각각 검색 시")
    @Test
    public void 아트워크_검색() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, true);

        String username = artwork1.getMember().getUsername();

        // 좋아요 표시한 사용자들 조회
        mockMvc.perform(
                get(String.format("/api/artworks/search?keyword=%s", artwork1.getTitle()))
            )
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(
                get(String.format("/api/artworks/search?keyword=%s", artwork1.getTags().split(",")[0]))
            )
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(
                get(String.format("/api/artworks/search?keyword=%s", artwork1.getMember().getName()))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("비공개 아트워크를 내 프로필 API로 조회 시 ")
    @Test
    public void 비공개_아트워크_내_프로필조회() throws Exception {
        Artwork artwork = createOrLoadArtwork(1, false);

        mockMvc.perform(
                get(String.format("/api/artworks/member/%s", artwork.getMember().getUsername()))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("관리자가 아트워크 전체 조회 시 공개여부와 상관없이 조회")
    @Test
    public void 관리자_전체_조회_시() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, true);
        Artwork artwork2 = createOrLoadArtwork(2, false);

        mockMvc.perform(
                get("/api/artworks")
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @DisplayName("비공개 아트워크 조회 시")
    @Test
    public void 비공개_조회_시() throws Exception {
        Artwork artwork1 = createOrLoadArtwork(1, false);

        mockMvc.perform(
                get("/api/artworks/" + artwork1.getId())
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 댓글 생성 시")
    @Test
    public void 아트워크_댓글_생성_시() throws Exception {
        Artwork artwork = createOrLoadArtwork();
        ArtworkCommentCreateDTO commentCreateDTO = new ArtworkCommentCreateDTO();
        commentCreateDTO.setContent("댓글 내용");

        mockMvc.perform(
                post("/api/artworks/" + artwork.getId() + "/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentCreateDTO))
            )
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 댓글 수정 시")
    @Test
    public void 아트워크_댓글_수정_시() throws Exception {
        Artwork artwork = createArtworkWithComment(2);
        Long commentId = artwork.getArtworkComments().get(0).getId();

        ArtworkCommentCreateDTO commentCreateDTO = new ArtworkCommentCreateDTO();
        commentCreateDTO.setContent("수정된 댓글 내용");

        mockMvc.perform(
                put("/api/artworks/" + artwork.getId() + "/comments/" + commentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentCreateDTO))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("작성자가 아닌 아트워크 댓글 수정 시")
    @Test
    public void 작성자아닌_아트워크_댓글_수정_시() throws Exception {
        createOrLoadMember("user", "ROLE_USER");
        Artwork artwork = createArtworkWithComment(2);
        Long commentId = artwork.getArtworkComments().get(0).getId();

        ArtworkCommentCreateDTO commentCreateDTO = new ArtworkCommentCreateDTO();
        commentCreateDTO.setContent("수정된 댓글 내용");

        mockMvc.perform(
                put("/api/artworks/" + artwork.getId() + "/comments/" + commentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentCreateDTO))
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user", role = "ADMIN")
    @DisplayName("관리자가 아트워크 댓글 수정 시")
    @Test
    public void 관리자_아트워크_댓글_수정_시() throws Exception {
        createOrLoadMember("user", "ROLE_ADMIN");
        Artwork artwork = createArtworkWithComment(2);
        Long commentId = artwork.getArtworkComments().get(0).getId();

        ArtworkCommentCreateDTO commentCreateDTO = new ArtworkCommentCreateDTO();
        commentCreateDTO.setContent("수정된 댓글 내용");

        mockMvc.perform(
                put("/api/artworks/" + artwork.getId() + "/comments/" + commentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentCreateDTO))
            )
            .andDo(print())
            .andExpect(status().isOk());
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 댓글 삭제 시")
    @Test
    public void 아트워크_댓글_삭제_시() throws Exception {
        Artwork artwork = createArtworkWithComment(5);
        Long commentId = artwork.getArtworkComments().get(0).getId();

        mockMvc.perform(
                delete("/api/artworks/" + artwork.getId() + "/comments/" + commentId)
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("작성자가 아닌데 아트워크 댓글 삭제 시")
    @Test
    public void 작성자가아닌_아트워크_댓글_삭제_시() throws Exception {
        createOrLoadMember("user", "ROLE_USER");
        Artwork artwork = createArtworkWithComment(5);
        Long commentId = artwork.getArtworkComments().get(0).getId();

        mockMvc.perform(
                delete("/api/artworks/" + artwork.getId() + "/comments/" + commentId)
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user", role = "ADMIN")
    @DisplayName("관리자가 아트워크 댓글 삭제 시")
    @Test
    public void 관리자_아트워크_댓글_삭제_시() throws Exception {
        createOrLoadMember("user", "ROLE_ADMIN");
        Artwork artwork = createArtworkWithComment(5);
        Long commentId = artwork.getArtworkComments().get(0).getId();

        mockMvc.perform(
                delete("/api/artworks/" + artwork.getId() + "/comments/" + commentId)
            )
            .andDo(print())
            .andExpect(status().isNoContent());
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 상세 조회 시 댓글도 같이 조회 된다")
    @Test
    public void 아트워크_상세조회_댓글() throws Exception {
        Artwork artwork = createArtworkWithComment(5);

        mockMvc.perform(
                get("/api/artworks/" + artwork.getId())
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testuser", role = "USER")
    @DisplayName("나만 보기가 설정된 다른 사용자의 아트워크를 조회 시 ")
    @Test
    public void 나만보기_다른_아트워크() throws Exception {
        Artwork artwork = createOrLoadArtwork(1, false);

        mockMvc.perform(
                get("/api/artworks/" + artwork.getId())
            )
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("나만 보기가 설정된 본인 아트워크를 조회 시 ")
    @Test
    public void 나만보기_아트워크() throws Exception {
        Artwork artwork = createOrLoadArtwork(1, false);

        mockMvc.perform(
                get("/api/artworks/" + artwork.getId())
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

}
