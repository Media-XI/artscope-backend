package com.example.codebase.domain.post.repository;


import com.example.codebase.domain.post.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}


