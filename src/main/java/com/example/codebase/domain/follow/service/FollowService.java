package com.example.codebase.domain.follow.service;

import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.entity.FollowIds;
import com.example.codebase.domain.follow.repository.FollowRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FollowService {

    private final FollowRepository followRepository;

    private final MemberRepository memberRepository;


    @Autowired
    public FollowService(FollowRepository followRepository, MemberRepository memberRepository) {
        this.followRepository = followRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void followMember(String username, String followUser) {
        Member followerUser = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Member followingUser = memberRepository.findByUsername(followUser).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        FollowIds followIds = FollowIds.of(followerUser, followingUser);
        followIds.valid();

        Optional<Follow> alreadyFollow = followRepository.findById(followIds);
        if (alreadyFollow.isPresent()) {
            throw new RuntimeException("이미 팔로잉 중입니다.");
        } else {
            followRepository.save(Follow.of(followerUser, followingUser));
        }
    }

    @Transactional
    public void unfollowMember(String username, String followUser) {
        Member followerUser = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Member followingUser = memberRepository.findByUsername(followUser).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        FollowIds followIds = FollowIds.of(followerUser, followingUser);

        Optional<Follow> alreadyFollow = followRepository.findById(followIds);
        if (alreadyFollow.isPresent()) {
            followRepository.delete(alreadyFollow.get());
        } else {
            throw new RuntimeException("팔로잉 중이 아닙니다.");
        }
    }
}
