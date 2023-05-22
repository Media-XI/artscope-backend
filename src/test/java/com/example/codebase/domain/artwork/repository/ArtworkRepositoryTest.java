package com.example.codebase.domain.artwork.repository;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.blog.entity.Post;
import com.example.codebase.domain.blog.repository.PostRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@ActiveProfiles("test")
class ArtworkRepositoryTest {

    @Autowired
    private ArtworkRepository artworkRepository;

    @Test
    @DisplayName("최근 일주일내 조회수 수가 많은 순으로 아트워크 3개 가져온다")
    void testFindTopByPopular() throws Exception{
        // given
        Artwork artwork = Artwork.builder()
                .title("title")
                .description("description")
                .visible(true)
                .views(10)
                .createdTime(LocalDateTime.now())
                .build();

        Artwork artwork1 = Artwork.builder()
                .title("title1")
                .description("description1")
                .createdTime(LocalDateTime.now().minusDays(1))
                .visible(true)
                .views(5)
                .build();

        Artwork artwork2 = Artwork.builder()
                .title("title2")
                .description("description2")
                .createdTime(LocalDateTime.now().minusDays(2))
                .visible(true)
                .views(1)
                .build();

        Artwork artwork3 = Artwork.builder()
                .title("title3")
                .description("description3")
                .createdTime(LocalDateTime.now().minusDays(8))
                .visible(true)
                .views(100)
                .build();
        artworkRepository.saveAll(List.of(artwork, artwork1, artwork2, artwork3));

        // when
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.of(0, 0, 0));
        LocalDateTime endDateTime = LocalDateTime.now();
        List<Artwork> topByPopular = artworkRepository.findTopByPopular(startDateTime, endDateTime, PageRequest.of(0, 3));

        // then
        assertThat(topByPopular.size()).isEqualTo(3);
        assertThat(topByPopular.get(0).getTitle()).isEqualTo("title");
        assertThat(topByPopular.get(0).getViews()).isEqualTo(10);
    }
}