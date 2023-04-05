package com.example.codebase.controller;

import com.example.codebase.domain.artwork.dto.ArtworkCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkUpdateDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.entity.ArtworkMediaType;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.auth.WithMockCustomUser;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
        dummy.setAuthorities(Collections.singleton(memberAuthority));

        Member save = memberRepository.save(dummy);
        memberAuthorityRepository.save(memberAuthority);
        return save;
    }

    @Transactional
    public Artwork createOrLoadArtwork() {
        return createOrLoadArtwork(1, true);
    }

    @Transactional
    public Artwork createOrLoadArtwork(int index, boolean isVisible) {
        Optional<Artwork> artwork = artworkRepository.findById(Long.valueOf(index));
        if (artwork.isPresent()) {
            return artwork.get();
        }
        ArtworkMedia artworkMedia = ArtworkMedia.builder()
                .artworkMediaType(ArtworkMediaType.image)
                .mediaUrl("url")
                .description("미디어 설명")
                .build();

        Artwork dummy = Artwork.builder()
                .title("아트워크_테스트" + index)
                .description("작품 설명")
                .visible(isVisible)
                .member(createOrLoadMember())
                .createdTime(LocalDateTime.now().plusSeconds(index))
                .build();
        dummy.addArtworkMedia(artworkMedia);

        return artworkRepository.save(dummy);
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 한개 등록")
    @Test
    public void test00() throws Exception {
        createOrLoadMember();

        ArtworkMediaCreateDTO mediaCreateDTO = new ArtworkMediaCreateDTO();
        mediaCreateDTO.setMediaType(ArtworkMediaType.image.toString());
        mediaCreateDTO.setMediaUrl("url");
        mediaCreateDTO.setDescription("미디어 설명");

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setMediaUrls(Collections.singletonList(mediaCreateDTO));

        mockMvc.perform(
                        post("/api/artworks")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 두개 등록")
    @Test
    public void test01() throws Exception {
        createOrLoadMember();

        List<ArtworkMediaCreateDTO> mediaCreateDTOList = new ArrayList<>();

        ArtworkMediaCreateDTO mediaCreateDTO1 = new ArtworkMediaCreateDTO();
        mediaCreateDTO1.setMediaType(ArtworkMediaType.image.toString());
        mediaCreateDTO1.setMediaUrl("url");
        mediaCreateDTO1.setDescription("미디어 설명1");
        mediaCreateDTOList.add(mediaCreateDTO1);

        ArtworkMediaCreateDTO mediaCreateDTO2 = new ArtworkMediaCreateDTO();
        mediaCreateDTO2.setMediaType(ArtworkMediaType.video.toString());
        mediaCreateDTO2.setMediaUrl("url");
        mediaCreateDTO2.setDescription("미디어 설명2");
        mediaCreateDTOList.add(mediaCreateDTO2);


        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setMediaUrls(mediaCreateDTOList);

        mockMvc.perform(
                        post("/api/artworks")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }


    @DisplayName("아트워크 전체 조회")
    @Test
    public void test02() throws Exception {
        createOrLoadArtwork(1, true);
        createOrLoadArtwork(2, false);
        createOrLoadArtwork(3, true);
        createOrLoadArtwork(4, true);

        mockMvc.perform(
                        get("/api/artworks?page=0&size=10")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("아트워크 단일 조회")
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

        ArtworkMediaCreateDTO mediaCreateDTO = new ArtworkMediaCreateDTO();
        mediaCreateDTO.setMediaType("test");
        mediaCreateDTO.setMediaUrl("url");
        mediaCreateDTO.setDescription("미디어 설명");

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(false);
        dto.setMediaUrls(Collections.singletonList(mediaCreateDTO));

        mockMvc.perform(
                        post("/api/artworks")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isForbidden());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 미디어 수정 시")
    @Test
    public void test07() throws Exception {
        Artwork artwork = createOrLoadArtwork();

        ArtworkMediaCreateDTO dto = new ArtworkMediaCreateDTO();
        dto.setMediaType(ArtworkMediaType.image.toString());
        dto.setMediaUrl("수정한 URL");
        dto.setDescription("수정한 설명");


        mockMvc.perform(
                        put(String.format("/api/artworks/%d/media/%d", artwork.getId(), artwork.getArtworkMedia().get(0).getId()))
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("일반 사용자 아트워크 삭제")
    @Test
    public void 일반사용자_아트워크_삭제() throws Exception {
        Artwork artwork = createOrLoadArtwork();

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
}