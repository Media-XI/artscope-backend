package com.example.codebase.domain.magazine.repository;

import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineWithIsLiked;
import com.example.codebase.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {

    @Query("SELECT m FROM Magazine m WHERE m.id = :id AND m.isDeleted = false")
    Optional<Magazine> findById(Long id);

    Page<Magazine> findByMember(Member member, PageRequest pageRequest);

    @Query("SELECT m FROM Magazine m LEFT JOIN Follow f ON (f.follower= :member) WHERE f.followingMember = m.member")
    Page<Magazine> findByMemberToFollowing(Member member, PageRequest pageRequest);


    @Query("SELECT m AS magazine, false AS isLiked FROM Magazine m WHERE m.id = :id AND m.isDeleted = false")
    Optional<MagazineWithIsLiked> findMagazineWithIsLikedById(Long id);

    @Query("SELECT m AS magazine, CASE WHEN ml.member.username = :username THEN true ELSE false END As isLiked " +
            "FROM Magazine m LEFT JOIN MagazineLike ml ON m = ml.magazine AND ml.member.username = :username " +
            "WHERE m.id = :id")
    Optional<MagazineWithIsLiked> findMagazineWithLikedByIdAndMember(Long id, String username);

    @Query("SELECT m AS magazine, false AS isLiked FROM Magazine m WHERE m.isDeleted = false")
    Page<MagazineWithIsLiked> findAllMagazineWithIsLiked(PageRequest pageRequest);

    @Query("SELECT m AS magazine, CASE WHEN ml.member.username = :username THEN true ELSE false END As isLiked " +
            "FROM Magazine m LEFT JOIN MagazineLike ml ON m = ml.magazine AND ml.member.username = :username")
    Page<MagazineWithIsLiked> findAllMagazineWithIsLikedByUsername(String username, PageRequest pageRequest);

    default Optional<MagazineWithIsLiked> findMagazineWithIsLiked(Long id, String username) {
        if (username != null) {
            return findMagazineWithLikedByIdAndMember(id, username);
        }
        return findMagazineWithIsLikedById(id);
    }

    default Page<MagazineWithIsLiked> findAllMagazineWithIsLiked(PageRequest pageRequest, String username) {
        if (username != null) {
            return findAllMagazineWithIsLikedByUsername(username, pageRequest);
        }
        return findAllMagazineWithIsLiked(pageRequest);
    }
}