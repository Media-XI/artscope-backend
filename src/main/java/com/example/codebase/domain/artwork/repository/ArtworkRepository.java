package com.example.codebase.domain.artwork.repository;

import com.example.codebase.domain.artwork.entity.Artwork;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisNoOpBindingRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    @Query("select a from Artwork a where a.visible = true")
    Page<Artwork> findAll(Pageable pageable);
    Optional<Artwork> findByIdAndMember_Username(Long id, String username);

    Page<Artwork> findAllByMember_Username(Pageable pageable, String username);

    // 최근 일주일내 조회수 수가 많은 순으로 N개 가져온다
    @Query("select a from Artwork a where a.visible = true and a.createdTime between ?1 and ?2 order by a.view desc")
    List<Artwork> findTopByPopular(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);


}
