package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.magazine.dto.MagazineCategoryRequest;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.repository.MagazineCategoryRepository;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
import com.example.codebase.domain.magazine.service.MagazineLikeService;
import com.example.codebase.domain.magazine.service.MagazineService;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        String categoryName = "카테고리" + random.nextInt(300);

        char randomChar1 = (char) ('a' + random.nextInt(26));
        char randomChar2 = (char) ('a' + random.nextInt(26));
        String categorySlug = String.valueOf(randomChar1) + randomChar2;

        MagazineCategoryRequest.Create request = new MagazineCategoryRequest.Create(categoryName, categorySlug, null);

        MagazineCategoryResponse.Create category = magazineCategoryService.createCategory(request);
        return magazineCategoryService.getEntity(category.getId());
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