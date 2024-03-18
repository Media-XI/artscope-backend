package com.example.codebase.domain.post.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.createdTime between ?1 and ?2 order by p.views desc")
    List<Post> findTopByPopular(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Query("SELECT p AS post, CASE WHEN pm.member = :member THEN true ELSE false END as isLiked " +
        "FROM Post p LEFT JOIN PostLikeMember pm ON p = pm.post AND pm.member = :member")
    Page<PostWithIsLiked> findAllWithIsLiked(Member member, Pageable pageable);

    @Query("select p from Post p LEFT JOIN PostLikeMember plm ON p.id = plm.post.id " +
        "where plm.likedTime > ?1 " +
        "group by p.id " +
        "order by p.likes desc")
    List<Post> findTop10LikedPostByWeek(LocalDateTime startDateTime);

    @Query("SELECT p FROM Post p WHERE p.content LIKE %?1%")
    Page<Post> findAllByKeywordContaining(String keyword, PageRequest pageRequest);

//    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락 -> 데이터 정합성은 보장하지만 트랜잭션 대기 시간 상승
    @Query("select p FROM Post p WHERE p.id = :id")
    Optional<Post> findById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true) // 쿼리 실행 후 쓰기 지연 저장소를 날리기전, 쓰기 지연 저장소에 저장된 쿼리를 날림
    @Query("UPDATE Post p SET p.likes = :likes WHERE p.id = :id")
    void updateLikes(Integer likes, Long id);
}
