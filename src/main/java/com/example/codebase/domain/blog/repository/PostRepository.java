package com.example.codebase.domain.blog.repository;

import com.example.codebase.domain.blog.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.createdTime between ?1 and ?2 order by p.view desc")
    List<Post> findTopByPopular(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
