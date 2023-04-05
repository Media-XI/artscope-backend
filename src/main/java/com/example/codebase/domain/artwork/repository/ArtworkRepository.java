package com.example.codebase.domain.artwork.repository;

import com.example.codebase.domain.artwork.entity.Artwork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    @Query("select a from Artwork a where a.visible = true")
    Page<Artwork> findAll(Pageable pageable);
    Optional<Artwork> findByIdAndMember_Username(Long id, String username);

    Page<Artwork> findAllByMember_Username(Pageable pageable, String username);
}
