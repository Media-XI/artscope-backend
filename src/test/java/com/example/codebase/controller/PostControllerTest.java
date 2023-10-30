package com.example.codebase.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.post.dto.PostCommentCreateDTO;
import com.example.codebase.domain.post.dto.PostCreateDTO;
import com.example.codebase.domain.post.dto.PostMediaCreateDTO;
import com.example.codebase.domain.post.dto.PostUpdateDTO;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostComment;
import com.example.codebase.domain.post.repository.PostCommentRepository;
import com.example.codebase.domain.post.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
    private PostCommentRepository commentRepository;

    @Autowired
    private ResourceLoader resourceLoader;

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
                .email("emailasdf")
                .name("test")
                .activated(true)
                .createdTime(LocalDateTime.now())
                .build();

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of(role));
        memberAuthority.setMember(dummy);
        dummy.addAuthority(memberAuthority);

        Member save = memberRepository.save(dummy);
        return save;
    }

    public Post createPost() {
        Member loadMember = createOrLoadMember("admin", "ROLE_ADMIN");

        Post post = Post.builder()
                .content("content")
                .author(loadMember)
                .createdTime(LocalDateTime.now())
                .build();
        return postRepository.save(post);
    }

    // 댓글있는 게시글 생성
    public Post createPostWithComment(int commentSize) {
        Member loadMember = createOrLoadMember("admin", "ROLE_ADMIN");

        Post post = Post.builder()
                .content("content")
                .author(loadMember)
                .createdTime(LocalDateTime.now())
                .build();
        postRepository.save(post);

        for (int i = 1; i <= commentSize; i++) {
            PostComment comment = PostComment.of(PostCommentCreateDTO.builder()
                    .content("댓글" + i)
                    .build(), loadMember);
            comment.setPost(post);
            commentRepository.save(comment);
        }

        return post;
    }

    public void createPosts(int size) {
        Member loadMember = createOrLoadMember("admin", "ROLE_ADMIN");

        for (int i = 0; i < size; i++) {
            Post post = Post.builder()
                    .content("내용" + i)
                    .author(loadMember)
                    .createdTime(LocalDateTime.now())
                    .build();

            postRepository.save(post);
        }
    }

    private byte[] createMockImageFile() throws IOException {
        File file = resourceLoader.getResource("classpath:test/img.jpg").getFile(); // TODO : 테스트용 이미지 파일
        return Files.readAllBytes(file.toPath());
    }


    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 생성 시")
    @Test
    void 포스트_생성() throws Exception {
        createOrLoadMember("admin", "ROLE_ADMIN");
        // given
        PostCreateDTO postCreateDTO = PostCreateDTO.builder()
                .content("내용")
                .build();

        mockMvc.perform(
                        multipart("/api/posts")
                                .file(new MockMultipartFile("dto", "", "application/json",
                                        objectMapper.writeValueAsBytes(postCreateDTO)))
                                .contentType("multipart/form-data")
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("미디어 첨부된 포스트 생성 시")
    @Test
    void 미디어_포스트_생성() throws Exception {
        // given
        createOrLoadMember("admin", "ROLE_ADMIN");

        PostMediaCreateDTO thumbnailCreateDTO = new PostMediaCreateDTO();
        thumbnailCreateDTO.setMediaType("image");

        MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg",
                createMockImageFile());

        PostMediaCreateDTO mediaCreateDTO = new PostMediaCreateDTO();
        mediaCreateDTO.setMediaType("image");

        MockMultipartFile mediaFile = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
                createMockImageFile());

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        mediaFiles.add(mediaFile);

        PostCreateDTO dto = PostCreateDTO.builder()
                .content("내용")
                .thumbnail(thumbnailCreateDTO)
                .medias(Collections.singletonList(mediaCreateDTO))
                .build();

        mockMvc.perform(
                        multipart("/api/posts")
//                                .file(thumbnailFile)
                                .file(mediaFiles.get(0))
                                .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                                .contentType("multipart/form-data")
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("썸네일 파일이 누락된 포스트 생성 시")
    @Test
    void 미디어_포스트_생성_에러() throws Exception {
        // given
        createOrLoadMember("admin", "ROLE_ADMIN");

        PostMediaCreateDTO thumbnailCreateDTO = new PostMediaCreateDTO();
        thumbnailCreateDTO.setMediaType("image");

        PostMediaCreateDTO mediaCreateDTO = new PostMediaCreateDTO();
        mediaCreateDTO.setMediaType("image");

        MockMultipartFile mediaFile = new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg",
                createMockImageFile());

        List<MockMultipartFile> mediaFiles = new ArrayList<>();
        mediaFiles.add(mediaFile);

        PostCreateDTO dto = PostCreateDTO.builder()
                .content("내용")
                .thumbnail(thumbnailCreateDTO)
                .medias(Collections.singletonList(mediaCreateDTO))
                .build();

        mockMvc.perform(
                        multipart("/api/posts")
//                                .file(thumbnailFile)
                                .file(mediaFiles.get(0))
                                .file(new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto)))
                                .contentType("multipart/form-data")
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @DisplayName("포스트 전체 조회 시")
    @Test
    void 포스트_전체_조회() throws Exception {
        // given
        createPosts(10);

        // when
        mockMvc.perform(
                        get("/api/posts?page=0&size=20")
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
                .content("내용 수정")
                .build();

        mockMvc.perform(
                        put("/api/posts/" + post.getId())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("본인이 작성하지 않은 포스트 수정 시")
    @Test
    void 작성자가_아닌_포스트_수정() throws Exception {
        Post post = createPost();

        PostUpdateDTO dto = PostUpdateDTO.builder()
                .content("내용 수정")
                .build();

        mockMvc.perform(
                        put("/api/posts/" + post.getId())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 삭제 시")
    @Test
    void 포스트_삭제() throws Exception {
        Post post = createPost();

        mockMvc.perform(
                        delete("/api/posts/" + post.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("본인이 작성하지 않은 포스트 삭제 시")
    @Test
    void 작성자가_아닌_포스트_삭제() throws Exception {
        Post post = createPost();

        mockMvc.perform(
                        delete("/api/posts/" + post.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 좋아요 시")
    @Test
    void 포스트_좋아요() throws Exception {
        Post post = createPost();

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 전체 조회 시 좋아요 여부와 함께")
    @Test
    void 포스트_전체_조회_좋아요여부() throws Exception {
        Post post = createPost();
        createPost();
        createPost();
        createPost();

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/posts?page=0&size=20")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 상세 조회 시")
    @Test
    void 포스트_상세_조회() throws Exception {
        Post post = createPost();

        mockMvc.perform(
                        get("/api/posts/" + post.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("좋아요한 포스트 상세 조회 시")
    @Test
    void 좋아요_포스트_상세_조회() throws Exception {
        Post post = createPost();

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/posts/" + post.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("댓글 여러개가 있는 게시글 상세 조회 시")
    @Test
    void 댓글달린_게시글_상세조회() throws Exception {
        Post post = createPostWithComment(3);

        mockMvc.perform(
                        get("/api/posts/" + post.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("포스트 좋아요 두번 시")
    @Test
    void 포스트_두번_좋아요() throws Exception {
        Post post = createPost();

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("게시글에 댓글 생성 시")
    @Test
    void 게시글_댓글_생성() throws Exception {
        Post post = createPost();

        PostCommentCreateDTO dto = PostCommentCreateDTO.builder()
                .content("댓글")
                .build();

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("댓글에 대댓글 생성")
    @Test
    void 대댓글_생성() throws Exception {
        Post post = createPost();

        PostCommentCreateDTO dto1 = PostCommentCreateDTO.builder()
                .content("댓글1")
                .build();

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto1))
                )
                .andDo(print())
                .andExpect(status().isCreated());

        PostCommentCreateDTO dto2 = PostCommentCreateDTO.builder()
                .content("댓글2")
                .parentCommentId(1L)
                .build();

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto2))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("멘션 대댓글 (3차) 생성")
    @Test
    void 멘션_대댓글_생성() throws Exception {
        // given
        Post post = createPostWithComment(5);
        PostComment level1 = post.getPostComment().get(0);
        PostComment level2 = PostComment.of(PostCommentCreateDTO.builder()
                .content("2차 대댓글입니다.")
                .build(), level1.getAuthor());
        level2.setParent(level1);
        level2.setPost(post);
        commentRepository.save(level2);

        // when
        PostCommentCreateDTO dto = PostCommentCreateDTO.builder()
                .content("3차 대댓글입니다.")
                .parentCommentId(level2.getId())
                .build();

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isCreated()); // then
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("게시물에 댓글을 수정할시")
    @Test
    void 게시글_댓글_수정() throws Exception {
        Post post = createPostWithComment(1);
        PostComment comment = post.getPostComment().get(0);

        PostUpdateDTO dto = PostUpdateDTO.builder()
                .content("수정한댓글")
                .build();

        mockMvc.perform(
                        put("/api/posts/comments/" + comment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("게시물에 댓글을 삭제할시")
    @Test
    void 게시물_댓글_삭제() throws Exception {
        Post post = createPostWithComment(2);
        PostComment comment = post.getPostComment().get(0);

        mockMvc.perform(
                        delete("/api/posts/comments/" + comment.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/posts/" + post.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "admin", role = "ADMIN")
    @DisplayName("멘션 대댓글 (3차) 삭제 시 ")
    @Test
    void 멘션_대댓글_삭제() throws Exception {
        // given
        Post post = createPostWithComment(5);
        PostComment level1 = post.getPostComment().get(0);
        PostComment level2 = PostComment.of(PostCommentCreateDTO.builder()
                .content("2차 대댓글입니다.")
                .build(), level1.getAuthor());
        level2.setParent(level1);
        level2.setPost(post);
        commentRepository.save(level2);

        PostComment level3 = PostComment.of(PostCommentCreateDTO.builder()
                .content("3차 (멘션) 대댓글입니다.")
                .build(), level1.getAuthor());
        level3.setParent(level2.getParent());
        level3.setPost(post);
        level3.setMentionUsername(level2.getAuthor().getUsername());
        commentRepository.save(level3);

        mockMvc.perform(
                        delete("/api/posts/comments/" + level1.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());

    }
}