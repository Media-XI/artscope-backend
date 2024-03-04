package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.curation.dto.CurationRequest;
import com.example.codebase.domain.curation.entity.Curation;
import com.example.codebase.domain.curation.repository.CurationRepository;
import com.example.codebase.domain.curation.service.CurationService;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
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

    @Transactional
    public void createCuration() {
        for (int i = 1; i <= 22; i++) {
            Curation curation = new Curation((long) i, null, LocalDateTime.now());
            curationRepository.save(curation);
        }
    }

    public void createCurationAndMegazine() {
        createCuration();


        for (int i = 1; i <= 22; i++) {
            Member member = createOrLoadMember();
            MagazineCategory category = createCategory();
            MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
            magazineRequest.setTitle("제목" + i);
            magazineRequest.setContent("내용" + i);
            magazineRequest.setCategoryId(category.getId());
            MagazineResponse.Get magazineResponse = magazineService.create(magazineRequest, member, category);
            Long magazineId = magazineResponse.getId();

            CurationRequest.Create curationRequest = new CurationRequest.Create();
            curationRequest.setMagazineId(magazineId);

            curationService.createCuration(curationRequest);
        }
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("큐레이션 생성")
    @Test
    void 큐레이션_생성() throws Exception {
        createOrLoadMember();
        createCuration();
        MagazineResponse.Get magazineResponse = createMagaizne(createOrLoadMember());
        Long magazineId = magazineResponse.getId();

        CurationRequest.Create curationRequest = new CurationRequest.Create();
        curationRequest.setMagazineId(magazineId);

        mockMvc.perform(post("/api/curations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(curationRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("큐레이션 생성 실패")
    @Test
    void 큐레이션_생성_실패() throws Exception {
        createOrLoadMember();
        MagazineResponse.Get magazineResponse = createMagaizne(createOrLoadMember());
        Long magazineId = magazineResponse.getId();

        CurationRequest.Create curationRequest = new CurationRequest.Create();
        curationRequest.setMagazineId(magazineId);

        mockMvc.perform(post("/api/curations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(curationRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("큐레이션이 22개 이상일때 추가할 시")
    @Test
    void 큐레이션_갯수가_22개_이상일때_추가시() throws Exception {
        createOrLoadMember();
        createCurationAndMegazine();

        MagazineResponse.Get magazineResponse = createMagaizne(createOrLoadMember());
        Long magazineId = magazineResponse.getId();

        CurationRequest.Create curationRequest = new CurationRequest.Create();
        curationRequest.setMagazineId(magazineId);

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
        createOrLoadMember();
        createCurationAndMegazine();

        MagazineResponse.Get magazineResponse = createMagaizne(createOrLoadMember());
        Long magazineId = magazineResponse.getId();

        CurationRequest.Update curationRequest = new CurationRequest.Update();
        curationRequest.setMagazineId(magazineId);
        curationRequest.setCurationId(12L);

        mockMvc.perform(post("/api/curations/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(curationRequest)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("큐레이션 삭제")
    @Test
    void 큐레이션_삭제() throws Exception {
        createOrLoadMember();
        createCurationAndMegazine();


        mockMvc.perform(delete("/api/curations/" + 22))
                        .andDo(print())
                .andExpect(status().isNoContent());
    }

    @DisplayName("큐레이션 전체 조회")
    @Test
    void 큐레이션_전체_조회() throws Exception {
        createCurationAndMegazine();

        mockMvc.perform(get("/api/curations"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("큐레이션의 매거진이 삭제됬을시")
    @Test
    void 큐레이션의_매거진이_삭제됬을시() throws Exception {
        createOrLoadMember();
        createCurationAndMegazine();

        for(int i = 1; i < 22; i++){
            magazineService.delete("testid", (long) i);
        }

        mockMvc.perform(get("/api/curations"))
                .andDo(print())
                .andExpect(status().isOk());

    }


}
