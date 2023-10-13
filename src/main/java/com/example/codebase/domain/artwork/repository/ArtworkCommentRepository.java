package com.example.codebase.domain.artwork.repository;


import com.example.codebase.domain.artwork.entity.ArtworkComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkCommentRepository extends JpaRepository<ArtworkComment, Long> {

}
