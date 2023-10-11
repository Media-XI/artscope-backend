package com.example.codebase.domain.post.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.createdTime between ?1 and ?2 order by p.views desc")
    List<Post> findTopByPopular(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Query("SELECT p AS post, CASE WHEN pm.member = :member THEN true ELSE false END as isLiked " +
            "FROM Post p LEFT JOIN PostLikeMember pm ON p = pm.post AND pm.member = :member")
    Page<PostWithIsLiked> findAllWithIsLiked(Member member, Pageable pageable);
}