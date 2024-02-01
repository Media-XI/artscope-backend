package com.example.codebase.domain.follow.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.follow.dto.FollowMemberDetailResponseDTO;
import com.example.codebase.domain.follow.dto.FollowMembersResponseDTO;
import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.entity.FollowIds;
import com.example.codebase.domain.follow.repository.FollowRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    @Transactional(readOnly = true)
    public FollowMembersResponseDTO getFollowingList(Optional<String> loginUsername, String targetUsername, PageRequest pageRequest) {
        Member loginMember = loginUsername.map(s -> memberRepository.findByUsername(s)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다.")))
                .orElse(null);

        List<FollowMemberDetailResponseDTO> followingUserResponses = new ArrayList<>();
        PageInfo pageInfo;

        if (loginMember != null) {
            Page<Follow> mutualFollowingUser = followRepository.findMutualFollowingByUserAndLoginUser(targetUsername, loginMember.getUsername(), pageRequest);
            mutualFollowingUser.getContent().forEach(follow -> followingUserResponses.add(FollowMemberDetailResponseDTO.of(follow.getFollower(), true)));

            if (mutualFollowingUser.getNumberOfElements() < pageRequest.getPageSize()) {
                Page<Follow> nonMutualFollowsPage = followRepository.findNotMutualFollowingByUserAndLoginUser(targetUsername, loginMember.getUsername(), pageRequest);
                nonMutualFollowsPage.getContent().forEach(follow -> followingUserResponses.add(FollowMemberDetailResponseDTO.of(follow.getFollower(), false)));
                pageInfo = PageInfo.from(nonMutualFollowsPage);

            } else {
                pageInfo = PageInfo.from(mutualFollowingUser);
            }
        } else {
            Page<Follow> followingUser = followRepository.findByFollowingUsername(targetUsername, pageRequest);
            followingUser.getContent().forEach(follow -> followingUserResponses.add(FollowMemberDetailResponseDTO.of(follow.getFollower(), false)));
            pageInfo = PageInfo.from(followingUser);

        }
        return FollowMembersResponseDTO.of(followingUserResponses, pageInfo);
    }

    @Transactional(readOnly = true)
    public FollowMembersResponseDTO getFollowerList(Optional<String> loginUsername, String targetUsername, PageRequest pageRequest) {
        Member loginMember = loginUsername.map(s -> memberRepository.findByUsername(s)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다.")))
                .orElse(null);

        List<FollowMemberDetailResponseDTO> followerUserResponse = new ArrayList<>();
        PageInfo pageInfo;

        if (loginMember != null) {
            Page<Follow> mutualFollowerUser = followRepository.findMutualFollowerByUserAndLoginUser(targetUsername, loginMember.getUsername(), pageRequest);
            mutualFollowerUser.getContent().forEach(follow -> followerUserResponse.add(FollowMemberDetailResponseDTO.of(follow.getFollow(), true)));

            if (mutualFollowerUser.getNumberOfElements() < pageRequest.getPageSize()) {
                Page<Follow> nonMutualFollowerUser = followRepository.findNotMutualFollowerByUserAndLoginUser(targetUsername, loginMember.getUsername(), pageRequest);
                nonMutualFollowerUser.getContent().forEach(follow -> followerUserResponse.add(FollowMemberDetailResponseDTO.of(follow.getFollow(), false)));
                pageInfo = PageInfo.from(nonMutualFollowerUser);
            } else {
                pageInfo = PageInfo.from(mutualFollowerUser);
            }
        } else {
            Page<Follow> followerUser = followRepository.findByFollowerUsername(targetUsername, pageRequest);
            followerUser.getContent().forEach(follow -> followerUserResponse.add(FollowMemberDetailResponseDTO.of(follow.getFollow(), false)));
            pageInfo = PageInfo.from(followerUser);
        }

        return FollowMembersResponseDTO.of(followerUserResponse, pageInfo);
    }
}
