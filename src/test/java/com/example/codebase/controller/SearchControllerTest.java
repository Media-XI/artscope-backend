package com.example.codebase.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.media.MediaType;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.repository.PostRepository;
import com.example.codebase.s3.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

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
        dummy.addAuthority(memberAuthority);

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
                    .artworkMediaType(MediaType.image)
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

    private byte[] createImageFile() throws IOException {
        File file = resourceLoader.getResource("classpath:test/img.jpg").getFile(); // TODO : 테스트용 이미지 파일
        return Files.readAllBytes(file.toPath());
    }

    public void createPosts(int size) {
        Member loadMember = createOrLoadMember();

        for (int i = 0; i < size; i++) {
            Post post = Post.builder()
                    .content("포스트 내용, 아트워크 좋아용" + i)
                    .author(loadMember)
                    .createdTime(LocalDateTime.now())
                    .build();

            postRepository.save(post);
        }
    }


    @DisplayName("통합 검색 시")
    @Test
    public void test() throws Exception {
        createOrLoadArtwork(10, true);
        createPosts(5);

        mockMvc.perform(
                        get("/api/search?keyword=아트워크&page=0&size=10")

                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}