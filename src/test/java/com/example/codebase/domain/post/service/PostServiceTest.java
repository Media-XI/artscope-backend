package com.example.codebase.domain.post.service;

import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.post.dto.PostResponseDTO;
import com.example.codebase.domain.post.dto.PostWithLikesResponseDTO;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Member createOrLoadMember(String username, String role) {
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

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of(role));
        memberAuthority.setMember(dummy);
        dummy.addAuthority(memberAuthority);

        Member save = memberRepository.saveAndFlush(dummy);
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


    @DisplayName("2명 이상의 사용자가 동시에 좋아요 요청 시")
    @Test
    void test1() throws Exception {
        // given
        Post post = createPost();
        int threadCount = 5;

        List<Member> members = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            members.add(createOrLoadMember("testid" + i, "ROLE_USER"));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        IntStream.range(0, threadCount).forEach((i) -> {
            executorService.execute(() -> {
                try {
                    PostResponseDTO dto = postService.likePost(post.getId(), members.get(i).getUsername());
                } finally {
                    latch.countDown();
                }
            });
        });
        latch.await(30, TimeUnit.SECONDS);

        PostWithLikesResponseDTO dto = postService.getPost(post.getId());
        assertEquals(threadCount, dto.getLikes());
    }

    @DisplayName("1명 이상의 사용자가 여러번 좋아요 요청 시")
    @Test
    void test2() throws Exception {
        // given
        Post post = createPost();
        int threadCount = 2;

        Member member = createOrLoadMember("testid" + 0, "ROLE_USER");
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        IntStream.range(0, threadCount).forEach((i) -> {
            try {
                Thread.sleep(500);
                executorService.execute(() -> {
                    PostResponseDTO dto = postService.likePost(post.getId(), member.getUsername());
                    System.out.println(i + " " + dto.getLikes());
                    latch.countDown();
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        latch.await(3000, TimeUnit.MILLISECONDS);

        PostWithLikesResponseDTO dto = postService.getPost(post.getId());
        System.out.println(dto.getLikes());
        assertEquals(1, dto.getLikes());
    }
}