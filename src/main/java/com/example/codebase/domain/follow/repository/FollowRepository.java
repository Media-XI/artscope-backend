package com.example.codebase.domain.follow.repository;

import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.entity.FollowIds;
import com.example.codebase.domain.follow.entity.FollowWithIsFollow;
import com.example.codebase.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, FollowIds> {


    @Query("SELECT f AS follow, " +
            "CASE WHEN f.following = :loginMember THEN '본인' " +
            "WHEN f2.follower = :loginMember THEN '팔로잉중' ELSE '논팔로잉' END as status " +
            "FROM Follow f LEFT JOIN Follow f2 ON f.following = f2.following " +
            "AND f2.follower = :loginMember " +
            "WHERE f.follower = :targetMember " +
            "ORDER BY CASE WHEN f.following = :loginMember THEN 1 " +
            "WHEN f2.follower = :loginMember THEN 2 ELSE 3 END, " +
            "f.followTime ASC")
    Page<FollowWithIsFollow> findFollowingByTargetMember(Member targetMember, Member loginMember, PageRequest pageRequest);


    @Query("SELECT f AS follow, " +
            "CASE WHEN  f.follower = :loginMember THEN '본인' " +
            "WHEN f2.follower = :loginMember THEN '팔로잉중' ELSE '논팔로잉' END as status " +
            "FROM Follow f LEFT JOIN Follow f2 ON f.follower = f2.following " +
            "AND f2.follower = :loginMember " +
            "WHERE f.following = :targetMember " +
            "ORDER BY CASE WHEN f.follower = :loginMember THEN 1 " +
            "WHEN f2.follower = :loginMember THEN 2 ELSE 3 END, " +
            "f.followTime ASC")
    Page<FollowWithIsFollow> findFollowerByTargetMember(Member targetMember, Member loginMember, PageRequest pageRequest);
}