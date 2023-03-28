package com.example.codebase.controller;

import com.example.codebase.domain.artwork.dto.ArtworkCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import com.example.codebase.domain.artwork.entity.MediaType;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public Member createMember() {
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

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 한개 등록")
    @Test
    public void test01() throws Exception {
        createMember();

        ArtworkMediaCreateDTO mediaCreateDTO = new ArtworkMediaCreateDTO();
        mediaCreateDTO.setMediaType(MediaType.image);
        mediaCreateDTO.setMediaUrl("url");

        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setMediaUrls(Collections.singletonList(mediaCreateDTO));

        mockMvc.perform(
                        post("/api/artwork")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("아트워크 두개 등록")
    @Test
    public void test02() throws Exception {
        createMember();

        List<ArtworkMediaCreateDTO> mediaCreateDTOList = new ArrayList<>();

        ArtworkMediaCreateDTO mediaCreateDTO1 = new ArtworkMediaCreateDTO();
        mediaCreateDTO1.setMediaType(MediaType.image);
        mediaCreateDTO1.setMediaUrl("url");
        mediaCreateDTOList.add(mediaCreateDTO1);

        ArtworkMediaCreateDTO mediaCreateDTO2 = new ArtworkMediaCreateDTO();
        mediaCreateDTO2.setMediaType(MediaType.video);
        mediaCreateDTO2.setMediaUrl("url");
        mediaCreateDTOList.add(mediaCreateDTO2);


        ArtworkCreateDTO dto = new ArtworkCreateDTO();
        dto.setTitle("아트워크_테스트");
        dto.setDescription("작품 설명");
        dto.setVisible(true);
        dto.setMediaUrls(mediaCreateDTOList);

        mockMvc.perform(
                        post("/api/artwork")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }


    @DisplayName("아트워크 전체 조회")
    @Test
    public void test03() throws Exception {
        mockMvc.perform(
                        get("/api/artwork")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}