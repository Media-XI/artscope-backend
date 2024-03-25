package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.magazine.dto.MagazineCategoryRequest;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
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
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private Random random = new Random();

    public MagazineCategory createCategoryAndLoad() {
        String categoryName = "카테고리" + random.nextInt(300);

        char randomChar1 = (char) ('a' + random.nextInt(26));
        char randomChar2 = (char) ('a' + random.nextInt(26));
        String categorySlug = new StringBuilder().append(randomChar1).append(randomChar2).toString();

        MagazineCategoryRequest.Create request = new MagazineCategoryRequest.Create(categoryName, categorySlug, null);

        MagazineCategoryResponse.Create category = magazineCategoryService.createCategory(request);
        return magazineCategoryService.getEntity(category.getId());
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
        mockMvc.perform(
                        put("/api/magazine-category/" + childCategory.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isOk());

        // then
        MagazineCategory updatedCategory = magazineCategoryService.getEntity(childCategory.getId());
        assertEquals(updateRequest.getName(), updatedCategory.getName());
        assertEquals(updateRequest.getSlug(), updatedCategory.getSlug());
        assertEquals(updateRequest.getParentId(), updatedCategory.getParent().getId());
    }

}