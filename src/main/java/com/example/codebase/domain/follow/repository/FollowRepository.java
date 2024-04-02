package com.example.codebase.domain.follow.repository;

import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.entity.FollowWithIsFollow;
import com.example.codebase.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {


    @Query("SELECT f AS follow, " +
            "CASE WHEN f.followingMember = :loginMember THEN 'self' " +
            "WHEN f2.follower = :loginMember THEN 'follow' ELSE 'none' END as status " +
            "FROM Follow f LEFT JOIN Follow f2 ON f.followingMember = f2.followingMember " +
            "AND f2.follower = :loginMember " +
            "WHERE f.follower = :targetMember " +
            "ORDER BY CASE WHEN f.followingMember = :loginMember THEN 1 " +
            "WHEN f2.follower = :loginMember THEN 2 ELSE 3 END, " +
            "f.followTime ASC")
    Page<FollowWithIsFollow> findFollowingByTargetMember(Member targetMember, Member loginMember, PageRequest pageRequest);


    @Query("SELECT f AS follow, " +
            "CASE WHEN  f.follower = :loginMember THEN 'self' " +
            "WHEN f2.follower = :loginMember THEN 'follow' ELSE 'none' END as status " +
            "FROM Follow f LEFT JOIN Follow f2 ON f.follower = f2.followingMember " +
            "AND f2.follower = :loginMember " +
            "WHERE f.followingMember = :targetMember " +
            "ORDER BY CASE WHEN f.follower = :loginMember THEN 1 " +
            "WHEN f2.follower = :loginMember THEN 2 ELSE 3 END, " +
            "f.followTime ASC")
    Page<FollowWithIsFollow> findFollowerByTargetMember(Member targetMember, Member loginMember, PageRequest pageRequest);


    Optional<Follow> findByFollowerAndFollowingMember(Member follower, Member followingMember);

    boolean existsByFollowerAndFollowingMember(Member follower, Member followingMember);
}