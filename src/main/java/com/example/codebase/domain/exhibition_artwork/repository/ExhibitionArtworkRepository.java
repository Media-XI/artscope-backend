package com.example.codebase.domain.exhibition_artwork.repository;

import com.example.codebase.domain.exhibition_artwork.entity.ExhibitionArtwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExhibitionArtworkRepository extends JpaRepository<ExhibitionArtwork, Long> {

    List<ExhibitionArtwork> findAllByExhibitionId(Long exhibitionId);

}
