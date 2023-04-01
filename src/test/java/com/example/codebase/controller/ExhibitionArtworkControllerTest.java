package com.example.codebase.controller;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.entity.MediaType;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.exhibition_artwork.entity.ExhibitionArtwork;
import com.example.codebase.domain.exhibition_artwork.entity.ExhibitionArtworkStatus;
import com.example.codebase.domain.exhibition_artwork.repository.ExhibitionArtworkRepository;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExhibitionArtworkControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    @Autowired
    private ExhibitionArtworkRepository exhibitionArtworkRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        dummy.setAuthorities(Collections.singleton(memberAuthority));

        Member save = memberRepository.save(dummy);
        memberAuthorityRepository.save(memberAuthority);
        return save;
    }

    public Artwork createOrLoadArtwork() {
        Optional<Artwork> testArtwork = artworkRepository.findById(1L);
        if (testArtwork.isPresent()) {
            return testArtwork.get();
        }
        ArtworkMedia artworkMedia = ArtworkMedia.builder()
                .mediaType(MediaType.video)
                .mediaUrl("url")
                .createdTime(LocalDateTime.now())
                .build();

        Artwork artwork = Artwork.builder()
                .title("작품 제목")
                .description("작품 설명")
                .member(createOrLoadMember())
                .visible(true)
                .createdTime(LocalDateTime.now())
                .build();
        artwork.addArtworkMedia(artworkMedia);

        return artworkRepository.save(artwork);
    }


    public ExhibitionArtwork createOrLoadExhibitionArtwork() {
        Optional<ExhibitionArtwork> save = exhibitionArtworkRepository.findById(1L);

        if (save.isPresent()) {
            return save.get();
        }

        ExhibitionArtwork exhibitionArtwork = ExhibitionArtwork.builder()
                .artwork(createOrLoadArtwork())
                .exhibition(createOrLoadExhibition())
                .status(ExhibitionArtworkStatus.submitted)
                .createdTime(LocalDateTime.now())
                .build();

        return exhibitionArtworkRepository.save(exhibitionArtwork);
    }

    public Exhibition createOrLoadExhibition() {
        Optional<Exhibition> save = exhibitionRepository.findById(1L);
        if (save.isPresent()) {
            return save.get();
        }

        Exhibition exhibition = Exhibition.builder()
                .title("공모전 제목")
                .description("공모전 설명")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1L))
                .createdTime(LocalDateTime.now())
                .member(createOrLoadMember())
                .build();

        return exhibitionRepository.save(exhibition);
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("해당 공모전 아트워크 등록")
    @Test
    public void test04() throws Exception {
        Exhibition exhibition = createOrLoadExhibition();
        Artwork artwork = createOrLoadArtwork();    // 전시회 생성

        mockMvc.perform(
                        post(String.format("/api/exhibitions/%d/artworks/%d", exhibition.getId(), artwork.getId()))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("해당 공모전 등록된 아트워크들 조회")
    @Test
    public void test05() throws Exception {
        ExhibitionArtwork exhibitionArtwork = createOrLoadExhibitionArtwork();

        mockMvc.perform(
                        get(String.format("/api/exhibitions/%d/artworks", exhibitionArtwork.getExhibition().getId()))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("관리자가 공모전에 제출한 아트워크 상태 변경 ")
    @Test
    public void test08() throws Exception {
        ExhibitionArtwork exhibitionArtwork = createOrLoadExhibitionArtwork();

        String status = "accepted";

        mockMvc.perform(
                        put(String.format("/api/exhibitions/%d/artworks/%d?status=%s", exhibitionArtwork.getExhibition().getId(), exhibitionArtwork.getArtwork().getId(), status))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("공모전에 제출한 아트워크 삭제 ")
    @Test
    public void test09() throws Exception {
        ExhibitionArtwork exhibitionArtwork = createOrLoadExhibitionArtwork();

        mockMvc.perform(
                        delete(String.format("/api/exhibitions/%d/artworks/%d", exhibitionArtwork.getExhibition().getId(), exhibitionArtwork.getArtwork().getId()))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


}