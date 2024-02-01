package com.example.codebase.domain.follow.repository;

import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.entity.FollowIds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, FollowIds> {

    @Query("SELECT f1 FROM Follow f1, Follow f2 WHERE f1.follower = f2.follower AND f1.follow.username = :targetUsername AND f2.follow.username = :loginUsername ORDER BY f2.followTime DESC")
    Page<Follow> findMutualFollowingByUserAndLoginUser(String targetUsername, String loginUsername, PageRequest pageRequest);

    @Query("SELECT f FROM Follow f WHERE f.follow.username = :targetUsername AND f.follower.username != :loginUsername ORDER BY f.followTime DESC ")
    Page<Follow> findNotMutualFollowingByUserAndLoginUser(String targetUsername, String loginUsername, PageRequest pageRequest);

    @Query("SELECT f FROM Follow f WHERE f.follow.username = :username ORDER BY f.followTime DESC")
    Page<Follow> findByFollowingUsername(String username, PageRequest pageRequest);

    @Query("SELECT f1 FROM Follow f1, Follow f2 WHERE f1.follower.username = :targetUsername AND f1.follow = f2.follower AND f2.follow.username = :username ORDER BY f2.followTime DESC")
    Page<Follow> findMutualFollowerByUserAndLoginUser(String targetUsername, String username, PageRequest pageRequest);

    @Query("SELECT f FROM Follow f WHERE f.follower.username =:targetUsername AND f.follower.username != :username ORDER BY f.followTime DESC")
    Page<Follow> findNotMutualFollowerByUserAndLoginUser(String targetUsername, String username, PageRequest pageRequest);

    @Query("SELECT f FROM Follow f WHERE f.follower.username = :targetUsername ORDER BY f.followTime DESC")
    Page<Follow> findByFollowerUsername(String targetUsername, PageRequest pageRequest);
}