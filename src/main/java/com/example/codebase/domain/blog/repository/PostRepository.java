package com.example.codebase.domain.blog.repository;

import com.example.codebase.domain.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
