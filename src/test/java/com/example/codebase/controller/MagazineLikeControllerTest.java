package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.magazine.dto.MagazineCategoryRequest;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.repository.MagazineCategoryRepository;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
import com.example.codebase.domain.magazine.service.MagazineLikeService;
import com.example.codebase.domain.magazine.service.MagazineService;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class MagazineLikeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MagazineService magazineService;

    @Autowired
    private MagazineLikeService magazineLikeService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MagazineRepository magazineRepository;

    @Autowired
    private MagazineCategoryRepository magazineCategoryRepository;

    @Autowired
    private MagazineCategoryService magazineCategoryService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Member member;
    Magazine magazine;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper.registerModule(new JavaTimeModule());

        member = createOrLoadMember("testid");
        magazine = createMagaizne(member);
    }

    @AfterEach
    public void tearDown() {
        magazineRepository.deleteAll();
        memberRepository.deleteAll();
    }

    public Member createOrLoadMember(){
        return createOrLoadMember("testid");
    }

    public Member createOrLoadMember(String username) {
        CreateMemberDTO createMemberDTO = new CreateMemberDTO();
        createMemberDTO.setUsername(username);
        createMemberDTO.setPassword("password");
        createMemberDTO.setName("name");
        createMemberDTO.setEmail("email");
        createMemberDTO.setAllowEmailReceive(true);

        memberService.createMember(createMemberDTO);
        return memberService.getEntity(username);
    }
    public Magazine createMagaizne(Member member) {
        MagazineCategory category = createCategory();

        Magazine saved = magazineRepository.saveAndFlush(Magazine.builder()
                .title("제목")
                .content("내용")
                .category(category)
                .member(member)
                .build());

        return saved;
    }

    public MagazineCategory createCategory() {
        Random random = new Random(System.currentTimeMillis());

        String categoryName = "카테고리" + random.nextInt(10000);

        char randomChar1 = (char) ('a' + random.nextInt(26));
        char randomChar2 = (char) ('a' + random.nextInt(26));
        String categorySlug = new StringBuilder().append(randomChar1).append(randomChar2).toString();

        MagazineCategoryRequest.Create request = new MagazineCategoryRequest.Create(categoryName, categorySlug, null);

        MagazineCategoryResponse.Create category = magazineCategoryService.createCategory(request);
        return magazineCategoryService.getEntity(category.getSlug());
    }

    @WithMockCustomUser(username = "testid")
    @DisplayName("매거진 좋아요 시")
    @Test
    void 매거진_좋아요() throws Exception {

        // when
        mockMvc.perform(
                        post("/api/magazines/{magazineId}/like", magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertEquals(1, magazineService.get(magazine.getId()).getLikes());
    }

    @WithMockCustomUser(username = "testid")
    @DisplayName("매거진 좋아요 후 전체 조회 시")
    @Test
    void 매거진_좋아요2() throws Exception {
        createMagaizne(member);
        createMagaizne(member);

        // when
        mockMvc.perform(
                        post("/api/magazines/{magazineId}/like", magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(1, magazineService.get(magazine.getId()).getLikes());

        // then
        mockMvc.perform(
                        get("/api/magazines")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid")
    @DisplayName("매거진 좋아요 후 상세 조회 시")
    @Test
    void 매거진_좋아요3() throws Exception {

        // when
        mockMvc.perform(
                        post("/api/magazines/{magazineId}/like", magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(1, magazineService.get(magazine.getId()).getLikes());

        // then
        mockMvc.perform(
                        get("/api/magazines/{magazineId}", magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }



    @WithMockCustomUser(username = "testid")
    @DisplayName("매거진 좋아요 취소 시")
    @Test
    void 매거진_좋아요_취소() throws Exception {
        // given
        magazineLikeService.like(magazine.getId(), member);

        // when
        mockMvc.perform(
                        post("/api/magazines/{magazineId}/unlike", magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertEquals(0, magazineService.get(magazine.getId()).getLikes());
    }

    @WithMockCustomUser(username = "testid")
    @DisplayName("매거진 좋아요 취소 후 전체 조회 시")
    @Test
    void 매거진_좋아요_취소2() throws Exception {
        // given
        createMagaizne(member);
        createMagaizne(member);
        magazineLikeService.like(magazine.getId(), member);

        // when
        mockMvc.perform(
                        post("/api/magazines/{magazineId}/unlike", magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(0, magazineService.get(magazine.getId()).getLikes());

        // then
        mockMvc.perform(
                        get("/api/magazines")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid2")
    @DisplayName("다른 사람이 좋아요한 게시물을 포함해 전체 조회 시")
    @Test
    void 좋아요3() throws Exception {
        // given
        createMagaizne(member);
        createMagaizne(member);
        magazineLikeService.like(magazine.getId(), member);

        // when then
        mockMvc.perform(
                        get("/api/magazines")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


    @WithMockCustomUser(username = "testid")
    @DisplayName("매거진 2번 좋아요 시")
    @Test
    void 매거진_2번_좋아요() throws Exception {

        // when
        mockMvc.perform(
                        post("/api/magazines/{magazineId}/like", magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(
                        post("/api/magazines/{magazineId}/like", magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertEquals(1, magazineService.get(magazine.getId()).getLikes());
    }


//    @WithMockCustomUser(username = "testid")
//    @DisplayName("한 유저가 동시에 2회 이상 매거진 좋아요 시, 한 요청만 성공 후 나머지 요청은 실패한다")
//    @Test
//    void 동시_매거진_좋아요() throws Exception {
//        int threadCount = 10;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        // when
//        IntStream.range(0, threadCount).forEach(e -> {
//            executorService.submit(() -> {
//                try {
//                    log.info("Thread {} started", e);
//                    TransactionStatus transactionStatus = transactionManager.getTransaction(null);
//                    magazineLikeService.like(magazine.getId(), member);
//                    transactionManager.commit(transactionStatus);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        });
//        latch.await();
//
//        // then
//        int likes = magazine.getLikes();
//        assertEquals(1, likes);
//    }


}