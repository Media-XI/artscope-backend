package com.example.codebase.controller;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.entity.ArtworkMediaType;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionMediaCreateDTO;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.example.codebase.domain.exhibition.entity.ExhibtionMediaType;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExhibitionControllerTest {
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
    private ExhibitionRepository exhibitionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
    public Member createOrLoadMember(int idx) {
        Optional<Member> testMember = memberRepository.findByUsername("testid" + idx);
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy = Member.builder()
                .username("testid" + idx)
                .password(passwordEncoder.encode("1234"))
                .email("email" + idx)
                .name("test" + idx)
                .activated(true)
                .createdTime(LocalDateTime.now())
                .build();

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of("ROLE_USER"));
        memberAuthority.setMember(dummy);
        dummy.setAuthorities(Collections.singleton(memberAuthority));

        Member save = memberRepository.save(dummy);
        // memberAuthorityRepository.save(memberAuthority);
        return save;
    }

    public Artwork createOrLoadArtwork() {
        Optional<Artwork> testArtwork = artworkRepository.findById(1L);
        if (testArtwork.isPresent()) {
            return testArtwork.get();
        }
        ArtworkMedia artworkMedia = ArtworkMedia.builder()
                .artworkMediaType(ArtworkMediaType.video)
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

    public Exhibition createOrLoadExhibition() {
        return createOrLoadExhibition(1);
    }

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
                .member(createOrLoadMember(idx))
                .build();
        exhibition.addExhibitionMedia(media);

        return exhibitionRepository.save(exhibition);
    }


    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("공모전 등록")
    @Test
    public void test01() throws Exception {
        createOrLoadMember();

        ExhibitionMediaCreateDTO createDTO = new ExhibitionMediaCreateDTO();
        createDTO.setMediaType(ExhibtionMediaType.image.name());
        createDTO.setMediaUrl("http://localhost:123");

        CreateExhibitionDTO dto = new CreateExhibitionDTO();
        dto.setTitle("공모전 제목");
        dto.setDescription("공모전 내용");
        dto.setLink("링크");
        dto.setStartDate(LocalDateTime.now());
        dto.setEndDate(LocalDateTime.now().plusMonths(1));
        dto.setMediaUrls(Collections.singletonList(createDTO));

        mockMvc.perform(
                        post("/api/exhibitions")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("비회원이 공모전 등록 시")
    @Test
    public void test02() throws Exception {
        createOrLoadMember();

        ExhibitionMediaCreateDTO createDTO = new ExhibitionMediaCreateDTO();
        createDTO.setMediaType(ExhibtionMediaType.image.name());
        createDTO.setMediaUrl("http://localhost:123");

        CreateExhibitionDTO dto = new CreateExhibitionDTO();
        dto.setTitle("공모전 제목");
        dto.setDescription("공모전 내용");
        dto.setLink("링크");
        dto.setStartDate(LocalDateTime.now());
        dto.setEndDate(LocalDateTime.now().plusMonths(1));
        dto.setMediaUrls(Collections.singletonList(createDTO));

        mockMvc.perform(
                        post("/api/exhibitions")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("공모전 전체 조회")
    @Test
    public void test03() throws Exception {
        createOrLoadExhibition(1);
        createOrLoadExhibition(2);
        createOrLoadExhibition(3);

        mockMvc.perform(
                        get("/api/exhibitions")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("공모전 수정")
    @Test
    public void test06() throws Exception {
        Exhibition exhibition = createOrLoadExhibition();

        CreateExhibitionDTO dto = new CreateExhibitionDTO();
        dto.setTitle("공모전 제목 수정");
        dto.setDescription("공모전 내용 수정");
        dto.setLink("링크 수정");
        dto.setStartDate(LocalDateTime.now());
        dto.setEndDate(LocalDateTime.now().plusMonths(1L));

        mockMvc.perform(
                        put(String.format("/api/exhibitions/%d", exhibition.getId()))
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "asdsad", role = "USER")
    @DisplayName("다른 사람이 해당 공모전 수정 시")
    @Test
    public void test07() throws Exception {
        Exhibition exhibition = createOrLoadExhibition();

        CreateExhibitionDTO dto = new CreateExhibitionDTO();
        dto.setTitle("공모전 제목 수정");
        dto.setDescription("공모전 내용 수정");
        dto.setLink("링크 수정");
        dto.setStartDate(LocalDateTime.now());
        dto.setEndDate(LocalDateTime.now().plusMonths(1L));

        mockMvc.perform(
                        put(String.format("/api/exhibitions/%d", exhibition.getId()))
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid1", role = "USER")
    @DisplayName("공모전 삭제")
    @Test
    public void test10() throws Exception {
        Exhibition exhibition = createOrLoadExhibition();

        mockMvc.perform(
                        delete(String.format("/api/exhibitions/%d", exhibition.getId()))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}