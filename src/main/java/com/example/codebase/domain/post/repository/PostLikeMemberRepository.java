package com.example.codebase.domain.post.repository;


import com.example.codebase.domain.post.entity.PostLikeMember;
import com.example.codebase.domain.post.entity.PostLikeMemberIds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeMemberRepository extends JpaRepository<PostLikeMember, PostLikeMemberIds> {

    Integer countByPostId(Long postId);
}
