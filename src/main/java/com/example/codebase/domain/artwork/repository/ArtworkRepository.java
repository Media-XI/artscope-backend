package com.example.codebase.domain.artwork.repository;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkWithIsLike;
import com.example.codebase.domain.member.entity.Member;
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

    @Query("SELECT a AS artwork, CASE WHEN a = alm.artwork THEN true ELSE false END as isLike " +
        "FROM Artwork a LEFT JOIN ArtworkLikeMember alm ON a = alm.artwork AND alm.member = :member " +
        "WHERE a.visible = true")
    Page<ArtworkWithIsLike> findAllWithIsLiked(Member member, Pageable pageable);

    Optional<Artwork> findByIdAndMember_Username(Long id, String username);

    Page<Artwork> findAllByMember_UsernameAndVisible(Pageable pageable, String username, boolean visible);

    // 최근 일주일내 조회수 수가 많은 순으로 N개 가져온다
    @Query("select a from Artwork a where a.visible = true and a.createdTime between ?1 and ?2 order by a.views desc")
    List<Artwork> findTopByPopular(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Query("SELECT a AS artwork, CASE WHEN alm.member = :member THEN true ELSE false END as isLike " +
        "FROM Artwork a LEFT JOIN ArtworkLikeMember alm ON a = alm.artwork AND alm.member = :member WHERE a.visible = :visible")
    Page<ArtworkWithIsLike> findAllWithIsLikeByMemberAndVisible(Member member, Boolean visible, Pageable pageable);

    @Query("SELECT a AS artwork, CASE WHEN alm.member = :member THEN true ELSE false END as isLike " +
        "FROM Artwork a LEFT JOIN ArtworkLikeMember alm ON a = alm.artwork AND alm.member = :member")
    Page<ArtworkWithIsLike> findAllWithIsLikeByMember(Member member, Pageable pageable);


    // TODO : 검색 쿼리 인덱스 사용하도록 개선 필요
    @Query("SELECT a " +
        "FROM Artwork a LEFT JOIN Member m ON a.member.id = m.id " +
        "WHERE a.visible = true AND (replace(a.title, ' ', '') LIKE %:keyword% OR a.tags LIKE %:keyword% OR replace(m.name, ' ', '') LIKE %:keyword% OR a.description LIKE %:keyword%)")
    Page<Artwork> findAllByKeywordContaining(String keyword, Pageable pageable);

    @Query("SELECT a FROM Artwork a LEFT JOIN ArtworkLikeMember alm ON a.id = alm.artwork.id " +
        "where alm.likedTime > ?1 " +
        "group by a.id " +
        "order by a.likes desc")
    List<Artwork> findTop10LikedArtworkByWeek(LocalDateTime startDateTime);

}
