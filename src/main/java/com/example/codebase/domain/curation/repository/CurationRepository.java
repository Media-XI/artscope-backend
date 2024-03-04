package com.example.codebase.domain.curation.repository;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.curation.entity.Curation;
import com.example.codebase.domain.magazine.entity.Magazine;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CurationRepository extends JpaRepository<Curation, Long> {

    @Query("SELECT c FROM Curation c WHERE c.magazine IS NOT NULL ORDER BY c.updatedTime DESC")
    List<Curation> findAllCuration();

    @Query("SELECT c FROM Curation c WHERE c.magazine IS NULL")
    Optional<Curation> findEmptyCuration(PageRequest pageRequest);

    @Query("SELECT c FROM Curation c ORDER BY c.updatedTime ASC")
    Curation findOldCuration(PageRequest pageRequest);

    default Curation findEmptyOrOldCuration() {
        PageRequest pageRequest = PageRequest.of(0, 1); // 1개만 조회
        Optional<Curation> emptyCuration = findEmptyCuration(pageRequest);

        if (emptyCuration.isPresent()) {
            return emptyCuration.get();
        }
        return findOldCuration(pageRequest);
    }

    @Query("SELECT c FROM Curation c WHERE c.magazine = :magazine")
    Optional<Curation> findByMagazine(Magazine magazine);
}
