package com.example.codebase.domain.post.repository;

import com.example.codebase.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("최근 일주일내 조회수 수가 많은 순으로 포스트 3개 가져온다")
    void testFindTopByPopular() throws Exception{
        // given
        Post pos1 = Post.builder()
                .content("description")
                .views(10)
                .createdTime(LocalDateTime.now())
                .build();

        Post pos2 = Post.builder()
                .content("description1")
                .createdTime(LocalDateTime.now().minusDays(1))
                .views(5)
                .build();

        Post pos3 = Post.builder()
                .content("description2")
                .createdTime(LocalDateTime.now().minusDays(2))
                .views(1)
                .build();

        Post pos4 = Post.builder()
                .content("description3")
                .createdTime(LocalDateTime.now().minusDays(8))
                .views(100)
                .build();
        postRepository.saveAll(List.of(pos1, pos2, pos3, pos4));

        // when
        List<Post> posts = postRepository.findTopByPopular(LocalDateTime.now().minusDays(7), LocalDateTime.now(), PageRequest.of(0, 3));


        // then
        assertThat(posts.size()).isEqualTo(3);
        assertThat(posts.get(0).getViews()).isEqualTo(10);
        assertThat(posts.get(1).getViews()).isEqualTo(5);
        assertThat(posts.get(2).getViews()).isEqualTo(1);


    }
}