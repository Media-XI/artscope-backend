package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.magazine.dto.MagazineCategoryRequest;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class MagazineCategoryControllerTest {

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

    public MagazineCategory createCategoryAndLoad() {
        Random random = new Random(System.currentTimeMillis());

        String categoryName = "카테고리" + random.nextInt(300);

        char randomChar1 = (char) ('a' + random.nextInt(26));
        char randomChar2 = (char) ('a' + random.nextInt(26));
        String categorySlug = new StringBuilder().append(randomChar1).append(randomChar2).toString();

        MagazineCategoryRequest.Create request = new MagazineCategoryRequest.Create(categoryName, categorySlug, null);

        MagazineCategoryResponse.Create category = magazineCategoryService.createCategory(request);
        return magazineCategoryService.getEntity(category.getSlug());
    }

    public MagazineResponse.Get createMagaizne(Member member, MagazineCategory category) {
        MagazineRequest.Create magazineRequest = new MagazineRequest.Create();
        magazineRequest.setTitle("제목");
        magazineRequest.setContent("내용");
        magazineRequest.setCategorySlug(category.getSlug());
        magazineRequest.setMetadata(Map.of(
                "color", "blue",
                "font", "godic"
        ));
        magazineRequest.setMediaUrls(List.of(
                "https://cdn.artscope.kr/local/1.jpg",
                "https://cdn.artscope.kr/local/2.jpg"
        ));

        return magazineService.createMagazine(magazineRequest, member, category, null);
    }

    public Member createMember(String username) {
        CreateMemberDTO createMemberDTO = new CreateMemberDTO();
        createMemberDTO.setUsername(username);
        createMemberDTO.setPassword("password");
        createMemberDTO.setName("name");
        createMemberDTO.setEmail("email" + "@" + username + ".com");
        createMemberDTO.setAllowEmailReceive(true);

        memberService.createMember(createMemberDTO);
        return memberService.getEntity(username);
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("매거진 카테고리 생성이 된다.")
    @Test
    public void createCategory() throws Exception {
        // given
        MagazineCategoryRequest.Create request = new MagazineCategoryRequest.Create("글", "word", null);

        // when
        mockMvc.perform(
                        post("/api/magazine-category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated());

        // then
        MagazineCategoryResponse.GetAll allCategory = magazineCategoryService.getAllCategory();
        assertTrue(allCategory.getCategories().stream()
                .map(MagazineCategoryResponse.Get::getName)
                .anyMatch(category -> category.equals(request.getName())));
    }

    @DisplayName("매거진 카테고리 전체가 조회 된다.")
    @Test
    public void getCategories() throws Exception {
        // given
        List<String> categoryNames = List.of("글", "IT", "사진");
        List<MagazineCategoryRequest.Create> categories = List.of(
                new MagazineCategoryRequest.Create("글", "word", null),
                new MagazineCategoryRequest.Create("IT", "it", null),
                new MagazineCategoryRequest.Create("사진", "photo", null)
        );

        magazineCategoryService.createCategory(categories.get(0));
        magazineCategoryService.createCategory(categories.get(1));
        magazineCategoryService.createCategory(categories.get(2));

        // when
        String response = mockMvc.perform(
                        get("/api/magazine-category")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineCategoryResponse.GetAll allCategory = objectMapper.readValue(response, MagazineCategoryResponse.GetAll.class); // json to object (역직렬화)
        assertTrue(allCategory.getCategories().stream()
                .map(MagazineCategoryResponse.Get::getName)
                .allMatch(categoryNames::contains));
    }

    @DisplayName("매거진 slug를 통해 하위 카테고리 조회가 된다. ")
    @Test
    public void getSubCategories() throws Exception {
        // given
        // 부모 카테고리 생성
        MagazineCategory parentCategory = createCategoryAndLoad();

        // 자식 카테고리 생성
        MagazineCategoryResponse.Create childCategory1 = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("자식1", "firstChild", parentCategory.getId())
        );

        MagazineCategoryResponse.Create childCategory2 = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("자식2", "secondChild", parentCategory.getId())
        );
        // 손자 카테고리 생성
        MagazineCategoryResponse.Create grandsonCategory = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("손자", "grandson", childCategory1.getId())
        );

        // when
        String response = mockMvc.perform(
                        get("/api/magazine-category/" + parentCategory.getSlug())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineCategoryResponse.GetAll subCategory = objectMapper.readValue(response, MagazineCategoryResponse.GetAll.class); // json to object (역직렬화)

        List<String> allNames = new ArrayList<>();
        for (MagazineCategoryResponse.Get category : subCategory.getCategories()) {
            // 부모 카테고리
            allNames.add(category.getName());

            // 자식 카테고리
            for (MagazineCategoryResponse.Get child : category.getChildrenCategories()) {
                allNames.add(child.getName());

                // 손자 카테고리
                for (MagazineCategoryResponse.Get grandchild : child.getChildrenCategories()) {
                    allNames.add(grandchild.getName());
                }
            }
        }

        boolean allCategoriesPresent = allNames.containsAll(List.of(childCategory1.getName(), childCategory2.getName(), grandsonCategory.getName()));

        assertTrue(allCategoriesPresent);
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("매거진 카테고리 삭제가 된다.")
    @Test
    public void deleteCategory() throws Exception {
        // given
        MagazineCategory category = createCategoryAndLoad();

        // when
        mockMvc.perform(
                        delete("/api/magazine-category/" + category.getId())
                )
                .andDo(print())
                .andExpect(status().isNoContent());


    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("매거진 카테고리 수정이 된다.")
    @Test
    public void updateCategory() throws Exception {
        // given
        // 부모 카테고리 생성
        MagazineCategory parentCategoryBefore = createCategoryAndLoad();

        MagazineCategory parentCategoryAfter = createCategoryAndLoad();

        // 자식 카테고리 생성
        MagazineCategoryResponse.Create childCategory = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("수정된카테고리", "changeCategory", parentCategoryBefore.getId())
        );

        MagazineCategoryRequest.Update updateRequest = new MagazineCategoryRequest.Update("수정된 글", "updated-word", parentCategoryAfter.getId());

        // when
        String response = mockMvc.perform(
                        patch("/api/magazine-category/" + childCategory.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineCategoryResponse.Get updatedCategory = objectMapper.readValue(response, MagazineCategoryResponse.Get.class);
        assertEquals(updateRequest.getName(), updatedCategory.getName());
        assertEquals(updateRequest.getSlug(), updatedCategory.getSlug());
        assertEquals(updateRequest.getParentId(), updatedCategory.getParentCategory().getId());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("카테고리는 생성시 깊이 2단계 까지만 생성이 가능하다")
    @Test
    public void 카테고리_생성시_깊이_2단계_초과시_에러() throws Exception {
        // given
        // 부모 카테고리 생성
        MagazineCategory parentCategory = createCategoryAndLoad();

        // 자식 카테고리 생성
        MagazineCategoryResponse.Create childDepth1 = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("깊이1", "depthOne", parentCategory.getId())
        );

        MagazineCategoryResponse.Create childDepth2 = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("깊이2", "depthTwo", childDepth1.getId())
        );
        // 깊이 3 카테고리 생성
        MagazineCategoryRequest.Create request = new MagazineCategoryRequest.Create("깊이3", "depthThree", childDepth2.getId());

        // when
        mockMvc.perform(
                        post("/api/magazine-category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("카테고리는 최대 2단계 까지만 생성 및 수정이 가능합니다.", result.getResolvedException().getMessage()));
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("카테고리는 생성시 깊이 2단계 까지만 수정이 가능하다 404")
    @Test
    public void 카테고리_수정시_깊이_2단계_초과시_에러() throws Exception {
        // given
        // 부모 카테고리 생성
        MagazineCategory parentCategoryBefore = createCategoryAndLoad();

        MagazineCategoryResponse.Create childDepth1 = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("깊이1", "depthOne", parentCategoryBefore.getId())
        );

        MagazineCategoryResponse.Create childDepth2 = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("깊이2", "depthTwo", childDepth1.getId())
        );

        // 자식 카테고리 생성
        MagazineCategoryResponse.Create updateCategoryBefore = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("수정될카테고리", "changeCategory", parentCategoryBefore.getId())
        );

        MagazineCategoryRequest.Update updateRequest = new MagazineCategoryRequest.Update("수정된카테고리", "updated-word", childDepth2.getId());

        // when
        mockMvc.perform(
                        patch("/api/magazine-category/" + updateCategoryBefore.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("카테고리는 최대 2단계 까지만 생성 및 수정이 가능합니다.", result.getResolvedException().getMessage()));
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("카테고리 생성시 slug가 중복되는 카테고리가 존재할 경우 에러 404")
    @Test
    public void 슬러그가_같은_카테고리_생성시_중복() throws Exception {
        // given
        MagazineCategoryResponse.Create existingCategory = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("기존카테고리", "category", null)
        );

        MagazineCategoryRequest.Create request = new MagazineCategoryRequest.Create("검증할카테고리", "category", null);

        // when
        mockMvc.perform(
                        post("/api/magazine-category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("슬러그가 중복되는 카테고리가 존재합니다.", result.getResolvedException().getMessage()));
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("카테고리 수정시 slug가 중복되는 카테고리가 존재할 경우 에러 404")
    @Test
    public void 슬러그가_같은_카테고리_수정시_중복() throws Exception {
        // given
        magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("비교카테고리", "category", null)
        );

        MagazineCategoryResponse.Create updateCategoryBefore = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("수정될카테고리", "changeCategory", null)
        );

        MagazineCategoryRequest.Create updateRequest = new MagazineCategoryRequest.Create("검증할카테고리", "category", null);

        // when
        mockMvc.perform(
                        patch("/api/magazine-category/" + updateCategoryBefore.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("슬러그가 중복되는 다른 카테고리가 존재합니다.", result.getResolvedException().getMessage()));
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("카테고리 삭제시 산하 메거진이 존재할 경우 에러 404")
    @Test
    public void 카테고리_삭제시_산하_매거진_존재시_에러() throws Exception {
        // given
        MagazineCategory parentCategory = createCategoryAndLoad();

        createMagaizne(createMember("admin"), parentCategory);

        // when
        mockMvc.perform(
                        delete("/api/magazine-category/" + parentCategory.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("해당 카테고리에 속한 매거진이 존재합니다.", result.getResolvedException().getMessage()));
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("카테고리 생성시 부모 카테고리 산하 이름이 같은 카테고리가 존재할 경우 에러 404")
    @Test
    public void 카테고리_생성시_같은부모_산하_이름_중복_에러() throws Exception {
        // given
        MagazineCategoryResponse.Create parentCategory = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("비교카테고리", "parentCategory", null)
        );

        MagazineCategoryResponse.Create existingCategory = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("이름이같은카테고리", "otherCategory", parentCategory.getId())
        );

        MagazineCategoryRequest.Create request = new MagazineCategoryRequest.Create("이름이같은카테고리", "category", parentCategory.getId());

        // when
        mockMvc.perform(
                        post("/api/magazine-category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("해당 부모 카테고리 산하 이름이 같은 카테고리가 존재합니다.", result.getResolvedException().getMessage()));
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("카테고리 수정시 부모 카테고리 산하 이름이 같은 카테고리가 존재할 경우 에러 404")
    @Test
    public void 카테고리_수정시_같은부모_산하_이름_중복_에러() throws Exception {
        // given
        MagazineCategoryResponse.Create parentCategory = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("비교카테고리", "parentCategory", null)
        );

        magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("이름이같은카테고리", "childCategory", parentCategory.getId())
        );

        MagazineCategoryResponse.Create changeCategory = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("수정할카테고리", "otherCategory", parentCategory.getId())
        );

        MagazineCategoryRequest.Update updateRequest = new MagazineCategoryRequest.Update("이름이같은카테고리", "otherCategory", parentCategory.getId());

        // when
        mockMvc.perform(
                        patch("/api/magazine-category/" + changeCategory.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("해당 부모 카테고리 산하에 같은 이름을 가진 다른 카테고리가 존재합니다.", result.getResolvedException().getMessage()));
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("카테고리 삭제시 카테고리를 가진 메거진이 있을경우 에러 404")
    @Test
    public void 카테고리_삭제시_매거진이_있을_경우_에러() throws Exception {
        // given
        MagazineCategory category = createCategoryAndLoad();

        Member member = createMember("admin");

        createMagaizne(member, category);

        // when
        mockMvc.perform(
                        delete("/api/magazine-category/" + category.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("해당 카테고리에 속한 매거진이 존재합니다.", result.getResolvedException().getMessage()));
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("매거진 카테고리를 수정시 자기 자신을 부모 카테고리로 둘수 없다.")
    @Test
    public void updateCategoryParentIsNotMe() throws Exception {
        // given
        MagazineCategory parentCategoryBefore = createCategoryAndLoad();

        // 부모 카테고리를 자기 자신을 참조하도록 변경
        MagazineCategoryResponse.Create childCategory = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("수정된카테고리", "changeCategory", parentCategoryBefore.getId())
        );

        MagazineCategoryRequest.Update updateRequest = new MagazineCategoryRequest.Update("수정된 글", "updated-word", childCategory.getId());

        // when
        mockMvc.perform(
                        patch("/api/magazine-category/" + childCategory.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                // then
                .andExpect(result -> assertEquals("부모 카테고리를 해당 카테고리로 설정할 수 없습니다.", result.getResolvedException().getMessage()));

    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("매거진 카테고리를 수정시 자기 자신의 슬러그 중복은 제외한다")
    @Test
    public void updateCategorySameSlug() throws Exception {
        // given
        MagazineCategoryResponse.Create category = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("카테고리", "category", null));

        MagazineCategoryRequest.Update updateRequest = new MagazineCategoryRequest.Update("수정된 글", "category", null);

        // when
        String response = mockMvc.perform(
                        patch("/api/magazine-category/" + category.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineCategoryResponse.Get updatedCategory = objectMapper.readValue(response, MagazineCategoryResponse.Get.class);
        assertEquals(updateRequest.getName(), updatedCategory.getName());
        assertEquals(updateRequest.getSlug(), updatedCategory.getSlug());
        assertEquals(updateRequest.getParentId(), updatedCategory.getParentCategory());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("매거진 카테고리를 수정시 자기 자신의 이름 중복은 제외한다")
    @Test
    public void updateCategorySameName() throws Exception {
        // given
        MagazineCategoryResponse.Create category = magazineCategoryService.createCategory(
                new MagazineCategoryRequest.Create("카테고리", "category", null));

        MagazineCategoryRequest.Update updateRequest = new MagazineCategoryRequest.Update("카테고리", "change-category", null);

        // when
        String response = mockMvc.perform(
                        patch("/api/magazine-category/" + category.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // then
        MagazineCategoryResponse.Get updatedCategory = objectMapper.readValue(response, MagazineCategoryResponse.Get.class);
        assertEquals(updateRequest.getName(), updatedCategory.getName());
        assertEquals(updateRequest.getSlug(), updatedCategory.getSlug());
        assertEquals(updateRequest.getParentId(), updatedCategory.getParentCategory());
    }
}

