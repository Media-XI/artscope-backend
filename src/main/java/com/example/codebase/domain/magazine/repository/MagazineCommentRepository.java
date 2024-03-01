package com.example.codebase.domain.magazine.repository;

import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineComment;
import com.example.codebase.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MagazineCommentRepository extends JpaRepository<MagazineComment, Long> {

    Optional<MagazineComment> findByIdAndMagazine(Long parentCommentId, Magazine magazine);


    List<MagazineComment> findByMagazine(Magazine magazine);
}
