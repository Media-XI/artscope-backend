package com.example.codebase.domain.post.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.createdTime between ?1 and ?2 order by p.views desc")
    List<Post> findTopByPopular(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Query("SELECT p AS post, CASE WHEN pm.member = :member THEN true ELSE false END as isLiked " +
            "FROM Post p LEFT JOIN PostLikeMember pm ON p = pm.post AND pm.member = :member")
    Page<PostWithIsLiked> findAllWithIsLiked(Member member, Pageable pageable);

    @Query("select p from Post p LEFT JOIN PostLikeMember plm ON p.id = plm.post.id " +
            "where plm.likedTime > ?1 " +
            "group by p.id " +
            "order by p.likes desc")
    List<Post> findTop10LikedPostByWeek(LocalDateTime startDateTime);

    @Query("SELECT p FROM Post p WHERE p.content LIKE %?1%")
    Page<Post> findAllByKeywordContaining(String keyword, PageRequest pageRequest);
}
