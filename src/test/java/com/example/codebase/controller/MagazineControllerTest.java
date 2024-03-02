package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.dto.MagazineCommentRequest;
import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
import com.example.codebase.domain.magazine.service.MagazineService;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class MagazineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MagazineCategoryService magazineCategoryService;

    @Autowired
    private MagazineService magazineService;

    @Autowired
    private MemberService memberService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Member createMember(String username) {
        CreateMemberDTO createMemberDTO = new CreateMemberDTO();
        createMemberDTO.setUsername(username);
        createMemberDTO.setPassword("password");
        createMemberDTO.setName("name");
        createMemberDTO.setEmail("email");
        createMemberDTO.setAllowEmailReceive(true);

        memberService.createMember(createMemberDTO);
        return memberService.getEntity(username);
    }

    public MagazineResponse.Get createMagaizne(Member member) {
        MagazineCategory category = createCategory();

        MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
        magazineRequest.setTitle("제목");
        magazineRequest.setContent("내용");
        magazineRequest.setCategoryId(category.getId());

        return magazineService.create(magazineRequest, member, category);
    }

    public MagazineCategory createCategory() {
        MagazineCategoryResponse.Get category = magazineCategoryService.createCategory("카테고리");
        return magazineCategoryService.getEntity(category.getId());
    }

    public MagazineResponse.Get createComment(MagazineResponse.Get magaizne, Member member, String comment) {
        MagazineCommentRequest.Create newComment = new MagazineCommentRequest.Create();
        newComment.setComment(comment);
        return magazineService.newMagazineComment(magaizne.getId(), member, newComment);
    }

    public MagazineResponse.Get createCommentHasChild(MagazineResponse.Get magaizne, Member member) {
        MagazineResponse.Get magazineResponse = createComment(magaizne, member, "1차 댓글");

        MagazineCommentRequest.Create newChildComment = new MagazineCommentRequest.Create();
        newChildComment.setComment("1차 댓글의 대댓글");
        newChildComment.setParentCommentId(magazineResponse.getFirstCommentId());
        return magazineService.newMagazineComment(magaizne.getId(), member, newChildComment);
    }


    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 생성이 된다.")
    @Test
    void 매거진_생성() throws Exception {
        // given
        createMember("testid");
        MagazineCategoryResponse.Get category = magazineCategoryService.createCategory("글");

        MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
        magazineRequest.setTitle("제목");
        magazineRequest.setContent("내용");
        magazineRequest.setCategoryId(category.getId());

        // when
        String response = mockMvc.perform(
                        post("/api/magazines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(magazineRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineResponse.Get magazine = objectMapper.readValue(response, MagazineResponse.Get.class);
        assertTrue(magazine.getId() > 0);
        assertEquals(magazine.getTitle(), magazineRequest.getTitle());
        assertEquals(magazine.getContent(), magazineRequest.getContent());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 생성시 카테고리가 없으면 400.")
    @Test
    void 매거진_생성_에러() throws Exception {
        // given
        createMember("testid");
        MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
        magazineRequest.setTitle("제목");
        magazineRequest.setContent("내용");
        magazineRequest.setCategoryId(0L);

        // when
        String content = mockMvc.perform(
                        post("/api/magazines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(magazineRequest))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(content.contains("해당 카테고리가 존재하지 않습니다.")); // TODO 에러 메시지를 한곳에서 관리할 필요가 있을듯
    }

    @DisplayName("매거진 전체 조회가 된다.")
    @Test
    void 매거진_전체_조회() throws Exception {
        // given
        Member author = createMember("testid");
        createMagaizne(author);
        createMagaizne(author);
        createMagaizne(author);
        createMagaizne(author);

        // when
        String response = mockMvc.perform(
                        get("/api/magazines")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineResponse.GetAll magazineList = objectMapper.readValue(response, MagazineResponse.GetAll.class);
        assertTrue(!magazineList.getMagazines().isEmpty());
    }

    @DisplayName("매거진 상세 조회가 된다.")
    @Test
    void 매거진_상세_조회() throws Exception {
        // given
        Member author = createMember("testid");
        MagazineResponse.Get magazine = createMagaizne(author);

        // when
        String response = mockMvc.perform(
                        get("/api/magazines/" + magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineResponse.Get magazineResponse = objectMapper.readValue(response, MagazineResponse.Get.class);
        assertEquals(magazine.getId(), magazineResponse.getId());
        assertEquals(magazine.getTitle(), magazineResponse.getTitle());
        assertEquals(magazine.getContent(), magazineResponse.getContent());
    }

    @DisplayName("매거진 상세 조회시 없는 매거진이면 404.")
    @Test
    void 매거진_상세_조회_에러() throws Exception {
        // when
        String content = mockMvc.perform(
                        get("/api/magazines/0")
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(content.contains("해당 매거진이 존재하지 않습니다."));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 수정이 된다.")
    @Test
    void 매거진_수정() throws Exception {
        // given
        Member author = createMember("testid");
        MagazineResponse.Get magazine = createMagaizne(author);

        MagazineRequest.Update magazineRequest = new MagazineRequest.Update();
        magazineRequest.setTitle("수정된 제목");
        magazineRequest.setContent("수정된 내용");

        // when
        String response = mockMvc.perform(
                        patch("/api/magazines/" + magazine.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(magazineRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineResponse.Get magazineResponse = objectMapper.readValue(response, MagazineResponse.Get.class);
        assertEquals(magazine.getId(), magazineResponse.getId());
        assertEquals(magazineRequest.getTitle(), magazineResponse.getTitle());
        assertEquals(magazineRequest.getContent(), magazineResponse.getContent());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 수정시 없는 매거진이면 404.")
    @Test
    void 매거진_수정_에러() throws Exception {
        // given
        MagazineRequest.Update magazineRequest = new MagazineRequest.Update();
        magazineRequest.setTitle("수정된 제목");
        magazineRequest.setContent("수정된 내용");

        // when
        String content = mockMvc.perform(
                        patch("/api/magazines/0")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(magazineRequest))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(content.contains("해당 매거진이 존재하지 않습니다."));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 삭제가 된다.")
    @Test
    void 매거진_삭제() throws Exception {
        // given
        Member author = createMember("testid");
        MagazineResponse.Get magazine = createMagaizne(author);

        // when
        mockMvc.perform(
                        delete("/api/magazines/" + magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        // then
        mockMvc.perform(
                        get("/api/magazines/" + magazine.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 삭제시 없는 매거진이면 404.")
    @Test
    void 매거진_삭제_에러() throws Exception {
        // when
        String content = mockMvc.perform(
                        delete("/api/magazines/0")
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(content.contains("해당 매거진이 존재하지 않습니다."));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 댓글 생성이 된다.")
    @Test
    void 매거진_댓글_생성() throws Exception {
        // given
        Member member = createMember("testid");
        MagazineResponse.Get magaizne = createMagaizne(member);
        MagazineCommentRequest.Create newComment = new MagazineCommentRequest.Create();
        newComment.setComment("댓글");

        // when
        String content = mockMvc.perform(
                        post("/api/magazines/" + magaizne.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComment))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(content.contains("댓글"));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 대댓글 생성이 된다.")
    @Test
    void 매거진_대댓글_생성() throws Exception {
        // given
        Member member = createMember("testid");
        MagazineResponse.Get magaizne = createMagaizne(member);
        MagazineResponse.Get comment = createComment(magaizne, member, "댓글");

        MagazineCommentRequest.Create newComment = new MagazineCommentRequest.Create();
        newComment.setComment("대댓글");
        newComment.setParentCommentId(comment.getFirstCommentId());

        // when
        String content = mockMvc.perform(
                        post("/api/magazines/" + magaizne.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComment))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(content.contains("대댓글"));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 언급 대댓글 생성이 된다.")
    @Test
    void 매거진_언급_대댓글_생성() throws Exception {
        // given
        Member member = createMember("testid");
        MagazineResponse.Get magaizne = createMagaizne(member);
        MagazineResponse.Get comment = createCommentHasChild(magaizne, member);

        MagazineCommentRequest.Create newComment = new MagazineCommentRequest.Create();
        newComment.setComment("언급!! - 댓글의 대댓글의 해당 대댓글의 대댓글 => 언급");
        newComment.setParentCommentId(comment.getFirstChildCommentOfFirstComment());

        // when
        String content = mockMvc.perform(
                        post("/api/magazines/" + magaizne.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComment))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(content.contains("대댓글"));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 댓글 수정이 된다.")
    @Test
    void 매거진_댓글_수정() throws Exception {
        // given
        Member member = createMember("testid");
        MagazineResponse.Get onlyMagaizne = createMagaizne(member);
        MagazineResponse.Get magazine = createComment(onlyMagaizne, member, "댓글");

        MagazineCommentRequest.Update updateComment = new MagazineCommentRequest.Update();
        updateComment.setComment("수정된 댓글");

        // when
        String content = mockMvc.perform(
                        patch("/api/magazines/" + magazine.getId() + "/comments/" + magazine.getFirstCommentId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateComment))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        assertTrue(content.contains("수정된 댓글"));
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 댓글 삭제가 된다.")
    @Test
    void 매거진_댓글_삭제() throws Exception {
        // given
        Member member = createMember("testid");
        MagazineResponse.Get onlyMagaizne = createMagaizne(member);
        MagazineResponse.Get magazine = createComment(onlyMagaizne, member, "댓글");

        Long commentId = magazine.getFirstCommentId();

        // when
        mockMvc.perform(
                        delete("/api/magazines/" + magazine.getId() + "/comments/" + commentId)
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        // then
        mockMvc.perform(
                        patch("/api/magazines/" + magazine.getId() + "/comments/" + commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"comment\": \"수정된 댓글\"}")
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 생성 시 미디어 첨부가 된다.")
    @Test
    void 매거진_미디어_생성() throws Exception {
        // given
        createMember("testid");
        MagazineCategoryResponse.Get category = magazineCategoryService.createCategory("글");

        MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
        magazineRequest.setTitle("제목");
        magazineRequest.setContent("내용");
        magazineRequest.setCategoryId(category.getId());
        magazineRequest.setMediaUrls(List.of(
                "https://cdn.artscope.kr/local/1.jpg",
                "https://cdn.artscope.kr/local/2.jpg"
        ));

        // when
        String response = mockMvc.perform(
                        post("/api/magazines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(magazineRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineResponse.Get magazine = objectMapper.readValue(response, MagazineResponse.Get.class);
        assertTrue(magazine.getId() > 0);
        assertEquals(magazine.getTitle(), magazineRequest.getTitle());
        assertEquals(magazine.getContent(), magazineRequest.getContent());
        assertEquals(magazine.getMediaUrls().size(), 2);
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 생성 시 잘못된 미디어 URL이면 400.")
    @Test
    void 매거진_미디어_잘못된_생성() throws Exception {
        // given
        createMember("testid");
        MagazineCategoryResponse.Get category = magazineCategoryService.createCategory("글");

        MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
        magazineRequest.setTitle("제목");
        magazineRequest.setContent("내용");
        magazineRequest.setCategoryId(category.getId());
        magazineRequest.setMediaUrls(List.of(
                "/local/1.jpg",
                "1.jpg"
        ));

        // when
        mockMvc.perform(
                        post("/api/magazines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(magazineRequest))
                )
                .andDo(print())
                // then
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("매거진 생성 시 미디어 최대 개수 이상 첨부 시 400.")
    @Test
    void 매거진_미디어_잘못된_생성2() throws Exception {
        // given
        createMember("testid");
        MagazineCategoryResponse.Get category = magazineCategoryService.createCategory("글");

        MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
        magazineRequest.setTitle("제목");
        magazineRequest.setContent("내용");
        magazineRequest.setCategoryId(category.getId());
        magazineRequest.setMediaUrls(List.of(
                "https://cdn.artscope.kr/local/1.jpg",
                "https://cdn.artscope.kr/local/2.jpg",
                "https://cdn.artscope.kr/local/3.jpg",
                "https://cdn.artscope.kr/local/4.jpg",
                "https://cdn.artscope.kr/local/5.jpg",
                "https://cdn.artscope.kr/local/6.jpg",
                "https://cdn.artscope.kr/local/7.jpg",
                "https://cdn.artscope.kr/local/8.jpg",
                "https://cdn.artscope.kr/local/9.jpg",
                "https://cdn.artscope.kr/local/10.jpg",
                "https://cdn.artscope.kr/local/11.jpg"
                ));

        // when
        mockMvc.perform(
                        post("/api/magazines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(magazineRequest))
                )
                .andDo(print())
                // then
                .andExpect(status().isBadRequest());
    }
}