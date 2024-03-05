package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.curation.dto.CurationRequest;
import com.example.codebase.domain.curation.dto.CurationResponse;
import com.example.codebase.domain.curation.repository.CurationRepository;
import com.example.codebase.domain.curation.service.CurationService;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
import com.example.codebase.domain.magazine.service.MagazineService;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
public class CurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MagazineCategoryService magazineCategoryService;

    @Autowired
    private MagazineService magazineService;

    @Autowired
    private CurationService curationService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MagazineRepository magazineRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private CurationRepository curationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Member createOrLoadMember() {
        return createOrLoadMember("testid", "ROLE_ADMIN");
    }

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

        return memberRepository.save(dummy);
    }

    public MagazineCategory createCategory() {
        MagazineCategoryResponse.Get category = magazineCategoryService.createCategory("카테고리");
        return magazineCategoryService.getEntity(category.getId());
    }

    public MagazineResponse.Get createMagaizne(Member member) {
        MagazineCategory category = createCategory();

        MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
        magazineRequest.setTitle("제목");
        magazineRequest.setContent("내용");
        magazineRequest.setCategoryId(category.getId());

        return magazineService.create(magazineRequest, member, category);
    }

    public CurationResponse.GetAll createCuration(Magazine magazine) {
        CurationRequest.Create curationRequest = new CurationRequest.Create();
        List<Long> magazineIds = new ArrayList<>();
        magazineIds.add(magazine.getId());
        curationRequest.setMagazineIds(magazineIds);

        return curationService.createCuration(curationRequest);
    }


    public CurationResponse.GetAll createCuration(Long megazineId) {
        CurationRequest.Create curationRequest = new CurationRequest.Create();

        List<Long> magazineIds = new ArrayList<>();
        magazineIds.add(megazineId);
        curationRequest.setMagazineIds(magazineIds);

        return curationService.createCuration(curationRequest);
    }

    @Transactional
    public MagazineResponse.Get createMegazineAndCuration(Member member) {
        MagazineResponse.Get magazineResponse = createMagaizne(member);
        createCuration(magazineResponse.getId());
        return magazineResponse;
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("큐레이션 생성")
    @Test
    void 큐레이션_생성() throws Exception {
        MagazineResponse.Get magazineResponse = createMagaizne(createOrLoadMember());
        List<Long> magazineIds = new ArrayList<>();
        magazineIds.add(magazineResponse.getId());

        CurationRequest.Create curationRequest = new CurationRequest.Create();
        curationRequest.setMagazineIds(magazineIds);

        mockMvc.perform(post("/api/curations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(curationRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("관리자가 아닐시 큐레이션 생성 실패")
    @Test
    void 관리자가_아닐시_큐레이션_생성_실패() throws Exception {
        MagazineResponse.Get magazineResponse = createMagaizne(createOrLoadMember());
        List<Long> magazineIds = new ArrayList<>();
        magazineIds.add(magazineResponse.getId());

        CurationRequest.Create curationRequest = new CurationRequest.Create();
        curationRequest.setMagazineIds(magazineIds);

        mockMvc.perform(post("/api/curations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(curationRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("이미 큐레이션이 존재할 때 생성할시")
    @Test
    void 큐레이션_생성시_이미_존재할시_업데이트() throws Exception {
        MagazineResponse.Get magazineResponse = createMegazineAndCuration(createOrLoadMember());

        List<Long> magazineIds = new ArrayList<>();
        magazineIds.add(magazineResponse.getId());

        CurationRequest.Create curationRequest = new CurationRequest.Create();
        curationRequest.setMagazineIds(magazineIds);

        mockMvc.perform(post("/api/curations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(curationRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("큐레이션 수정")
    @Test
    void 큐레이션_수정() throws Exception {
        Member member = createOrLoadMember();
        MagazineResponse.Get megazine수정전 = createMagaizne(member);
        MagazineResponse.Get megazine수정후 = createMagaizne(member);

        CurationResponse.GetAll curationResponse = createCuration(megazine수정전.getId());

        CurationRequest.Update curationRequest = new CurationRequest.Update();
        curationRequest.setMagazineId(megazine수정후.getId());
        curationRequest.setCurationId(curationResponse.getCurations().get(0).getCurationsId());

        mockMvc.perform(patch("/api/curations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(curationRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("이미 큐레이팅된 매거진으로 수정할시")
    @Test
    void 이미_큐레이팅된_매거진이_존재할때_수정시_에러() throws Exception {
        Member member = createOrLoadMember();
        MagazineResponse.Get megazine수정전 = createMagaizne(member);
        MagazineResponse.Get megazine큐레이션된 = createMagaizne(member);

        CurationResponse.GetAll curationResponse = createCuration(megazine수정전.getId());
        createCuration(megazine큐레이션된.getId());

        CurationRequest.Update curationRequest = new CurationRequest.Update();
        curationRequest.setMagazineId(megazine큐레이션된.getId());
        curationRequest.setCurationId(curationResponse.getCurations().get(0).getCurationsId());

        mockMvc.perform(patch("/api/curations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(curationRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("큐레이션 삭제")
    @Test
    void 큐레이션_삭제() throws Exception {
        MagazineResponse.Get magazineResponse = createMegazineAndCuration(createOrLoadMember());
        Long megazineId = magazineResponse.getId();

        CurationResponse.GetAll curationResponse = createCuration(megazineId);
        Long curationsId = curationResponse.getCurations().get(0).getCurationsId();

        mockMvc.perform(delete("/api/curations/" + curationsId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("큐레이션 전체 조회")
    @Test
    void 큐레이션_() throws Exception {
        Member member = createOrLoadMember();
        createMegazineAndCuration(member);
        createMegazineAndCuration(member);
        createMegazineAndCuration(member);
        createMegazineAndCuration(member);
        createMegazineAndCuration(member);
        createMegazineAndCuration(member);
        createMegazineAndCuration(member);

        mockMvc.perform(get("/api/curations"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("큐레이션의 매거진이 삭제됬을시")
    @Test
    void 큐레이션의_매거진이_삭제됬을시() throws Exception {
        MagazineResponse.Get magazineResponse = createMegazineAndCuration(createOrLoadMember());
        Long megazineId = magazineResponse.getId();

        magazineService.delete("testid", megazineId);

        mockMvc.perform(get("/api/curations"))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
