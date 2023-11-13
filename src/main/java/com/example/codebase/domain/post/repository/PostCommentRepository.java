package com.example.codebase.domain.post.repository;


import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    Optional<PostComment> findByIdAndPost(Long id, Post post);

    @Query("SELECT COUNT(pc) FROM PostComment pc WHERE pc.post = :post")
    int countByPost(Post post);
}


