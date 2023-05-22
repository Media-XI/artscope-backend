package com.example.codebase.controller;

import com.example.codebase.config.S3MockConfig;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.blog.dto.PostCreateDTO;
import com.example.codebase.domain.blog.dto.PostUpdateDTO;
import com.example.codebase.domain.blog.entity.Post;
import com.example.codebase.domain.blog.repository.PostRepository;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.s3.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public Member createOrLoadMember(String username, String role) {
        Optional<Member> testMember = memberRepository.findByUsername(username);
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy = Member.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email("email")
                .name("test")
                .activated(true)
                .createdTime(LocalDateTime.now())
                .build();

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of(role));
        memberAuthority.setMember(dummy);
        dummy.setAuthorities(Collections.singleton(memberAuthority));

        Member save = memberRepository.save(dummy);
        memberAuthorityRepository.save(memberAuthority);
        return save;
    }

    public Post createPost() {
        Member loadMember = createOrLoadMember("admin", "ROLE_ADMIN");

        Post post = Post.builder()
                .title("title")
                .content("content")
                .author(loadMember)
                .createdTime(LocalDateTime.now())
                .build();
        return postRepository.save(post);
    }

    public void createPosts(int size) {
        Member loadMember = createOrLoadMember("admin", "ROLE_ADMIN");

        for (int i = 0; i < size; i++) {
            Post post = Post.builder()
                    .title("제목" + i)
                    .content("내용" + i)
                    .author(loadMember)
                    .createdTime(LocalDateTime.now())
                    .build();

            postRepository.save(post);
        }
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 생성 시")
    @Test
    void 포스트_생성() throws Exception {
        createOrLoadMember("admin", "ROLE_ADMIN");
        // given
        PostCreateDTO postCreateDTO = PostCreateDTO.builder()
                .title("제목")
                .content("내용")
                .build();

        mockMvc.perform(
                        post("/api/post")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(postCreateDTO))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("포스트 전체 조회 시")
    @Test
    void 포스트_전체_조회() throws Exception {
        // given
        createPosts(10);

        // when
        mockMvc.perform(
                        get("/api/post?page=0&size=20")
                )
                .andDo(print())
                .andExpect(status().isOk()); // then
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 수정 시")
    @Test
    void 포스트_수정() throws Exception {
        Post post = createPost();

        PostUpdateDTO dto = PostUpdateDTO.builder()
                .title("제목 수정")
                .content("내용 수정")
                .build();

        mockMvc.perform(
                        put("/api/post/" + post.getId())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 삭제 시")
    @Test
    void 포스트_삭제() throws Exception {
        Post post = createPost();

        mockMvc.perform(
                        delete("/api/post/" + post.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


}