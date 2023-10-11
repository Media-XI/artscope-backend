package com.example.codebase.controller;

import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.entity.ArtworkMediaType;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.example.codebase.domain.exhibition.entity.ExhibtionMediaType;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.repository.PostRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class FeedControllerTest {

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
    private PostRepository postRepository;

    @Autowired
    private ExhibitionRepository exhibitionRepository;

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

    @Transactional
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
        memberAuthorityRepository.save(memberAuthority);
        return save;
    }

    @Transactional
    public Artwork createOrLoadArtwork(int index, boolean isVisible, int mediaSize) throws IOException {
        Optional<Artwork> artwork = artworkRepository.findById(Long.valueOf(index));
        if (artwork.isPresent()) {
            return artwork.get();
        }

        List<ArtworkMedia> artworkMediaList = new ArrayList<>();
        for (int i = 0; i < mediaSize; i++) {
            String url = "https://test.com/image.jpg";

            ArtworkMedia artworkMedia = ArtworkMedia.builder()
                    .artworkMediaType(ArtworkMediaType.image)
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
    public Post createPost() {
        Member loadMember = createOrLoadMember();

        Post post = Post.builder()
                .content("content")
                .author(loadMember)
                .createdTime(LocalDateTime.now())
                .build();
        return postRepository.save(post);
    }

    @Transactional
    public Exhibition createOrLoadExhibition(int idx) {
        Optional<Exhibition> save = exhibitionRepository.findById(Long.valueOf(idx));
        if (save.isPresent()) {
            return save.get();
        }

        ExhibitionMedia media = ExhibitionMedia.builder()
                .mediaUrl("url" + idx)
                .exhibtionMediaType(ExhibtionMediaType.image)
                .createdTime(LocalDateTime.now())
                .build();

        Exhibition exhibition = Exhibition.builder()
                .title("공모전 제목" + idx)
                .description("공모전 설명" + idx)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(idx))
                .createdTime(LocalDateTime.now())
                .member(createOrLoadMember())
                .build();
        exhibition.addExhibitionMedia(media);

        return exhibitionRepository.save(exhibition);
    }

    @DisplayName("피드 생성")
    @Test
    public void createFeed() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);
        createOrLoadArtwork(2, true, 1);
        createOrLoadArtwork(3, true, 1);
        createOrLoadArtwork(4, true, 1);
        createOrLoadArtwork(5, true, 1);
        createOrLoadArtwork(6, true, 1);
        createOrLoadArtwork(7, true, 1);
        createOrLoadArtwork(8, true, 1);
        createOrLoadArtwork(9, true, 1);
        createOrLoadArtwork(10, true, 1);

        // Post 생성 및 저장
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();

        // 전시 생성 및 저장
        createOrLoadExhibition(1);
        createOrLoadExhibition(2);
        createOrLoadExhibition(3);
        createOrLoadExhibition(4);

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 생성 시 일부 데이터 없을 떄 ")
    @Test
    public void createFeed_일부데이터없이() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);
        createOrLoadArtwork(2, true, 1);
        createOrLoadArtwork(3, true, 1);
        createOrLoadArtwork(4, true, 1);
        createOrLoadArtwork(5, true, 1);
        createOrLoadArtwork(6, true, 1);
        createOrLoadArtwork(7, true, 1);
        createOrLoadArtwork(8, true, 1);
        createOrLoadArtwork(9, true, 1);
        createOrLoadArtwork(10, true, 1);

        // Post 생성 및 저장
        createPost();
        createPost();

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "1")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 생성 시 일부 데이터가 없다면 ")
    @Test
    public void createFeed2() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);

        // 전시 생성 및 저장
        createOrLoadExhibition(1);

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 생성 시 전체 데이터가 없다면 ")
    @Test
    public void createFeed3() throws Exception {

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 조회 시 page가 0이면 에러 발생")
    @Test
    public void createFeed4() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);

        // Post 생성 및 저장
        createPost();

        // 전시 생성 및 저장
        createOrLoadExhibition(1);

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("일부 포스트, 아트워크 좋아요 여부에 따른 피드 조회")
    @Test
    public void createFeed5() throws Exception {
        Artwork artwork = createOrLoadArtwork(1, true, 1);
        createOrLoadArtwork(2, true, 1);

        // Post 생성 및 저장
        Post post = createPost();
        createPost();

        createOrLoadExhibition(1);

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/artworks/" + artwork.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }
}