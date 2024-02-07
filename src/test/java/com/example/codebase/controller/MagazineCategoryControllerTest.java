package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("매거진 카테고리 생성이 된다.")
    @Test
    public void createCategory() throws Exception {
        // given
        String name = "글";

        // when
        mockMvc.perform(
                        post("/api/magazine-category")
                                .param("name", name)
                )
                .andDo(print())
                .andExpect(status().isCreated());

        // then
        MagazineCategoryResponse.GetAll allCategory = magazineCategoryService.getAllCategory();
        assertTrue(allCategory.getCategories().stream()
                .anyMatch(category -> category.getName().equals(name)));
    }

    @DisplayName("매거진 카테고리 전체가 조회 된다.")
    @Test
    public void getCategories() throws Exception {
        // given
        List<String> categoryNames = List.of("글", "IT", "사진");
        magazineCategoryService.createCategory(categoryNames.get(0));
        magazineCategoryService.createCategory(categoryNames.get(1));
        magazineCategoryService.createCategory(categoryNames.get(2));
        magazineCategoryService.createCategory("음악");

        // when
        String response = mockMvc.perform(
                        get("/api/magazine-category")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(); // json

        // then
        MagazineCategoryResponse.GetAll allCategory = objectMapper.readValue(response, MagazineCategoryResponse.GetAll.class); // json to object (역직렬화)
        assertTrue(allCategory.getCategories().stream()
                .anyMatch(category -> categoryNames.contains(category.getName())));
    }

}