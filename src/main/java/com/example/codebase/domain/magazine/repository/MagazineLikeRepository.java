package com.example.codebase.domain.magazine.repository;

import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineLike;
import com.example.codebase.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MagazineLikeRepository extends JpaRepository<MagazineLike, Long> {

    @Query("select ml from MagazineLike ml where ml.magazine = :magazine and ml.member = :member")
    Optional<MagazineLike> findByIds(Magazine magazine, Member member);
}
