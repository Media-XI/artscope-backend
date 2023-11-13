package com.example.codebase.domain.post.repository;

import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class PostCommentRepositoryTest {

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;


    public Member createOrLoadMember(String username, String role) {
        Optional<Member> testMember = memberRepository.findByUsername(username);
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy = Member.builder()
            .username(username)
            .email("test@email.com")
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

    @Test
    @DisplayName("댓글 생성 테스트")
    public void test() throws Exception {

        Post post = Post.builder()
            .content("content")
            .createdTime(LocalDateTime.now())
            .build();

        PostComment newComment = PostComment.builder()
            .content("content")
            .post(post)
            .createdTime(LocalDateTime.now())
            .build();

        post.addComment(newComment);
        postRepository.save(post);
    }


}