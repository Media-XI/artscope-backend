package com.example.codebase.domain.follow.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.follow.dto.FollowMemberDetailResponseDTO;
import com.example.codebase.domain.follow.dto.FollowMembersResponseDTO;
import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.entity.FollowWithIsFollow;
import com.example.codebase.domain.follow.repository.FollowRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;

    private final MemberRepository memberRepository;

    @Autowired
    public FollowService(FollowRepository followRepository, MemberRepository memberRepository) {
        this.followRepository = followRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void followMember(String username, String followMember) {
        Member follower = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Member following = memberRepository.findByUsername(followMember).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        checkFollowing(follower, following);

        followRepository.save(Follow.of(follower, following));
    }

    private void checkFollowing(Member follower, Member following) {
        if (followRepository.existsByFollowerAndFollowingMember(follower, following)) {
            throw new RuntimeException("이미 팔로잉 중입니다.");
        }
    }

    @Transactional
    public void unfollowMember(String username, String followMember) {
        Member follower = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Member following = memberRepository.findByUsername(followMember).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        Optional<Follow> alreadyFollow = followRepository.findByFollowerAndFollowingMember(follower, following);
        if (alreadyFollow.isEmpty()) {
            throw new RuntimeException("팔로잉 중이 아닙니다.");
        }
        followRepository.delete(alreadyFollow.get());
    }


    public FollowMembersResponseDTO getFollowingList(Optional<String> loginUsername, String targetUsername, PageRequest pageRequest) {
        Member targetMember = memberRepository.findByUsername(targetUsername).orElseThrow(NotFoundMemberException::new);
        Member loginMember = loginUsername.map(s -> memberRepository.findByUsername(s)
                .orElseThrow(NotFoundMemberException::new))
                .orElse(null);

        Page<FollowWithIsFollow> followingList = followRepository.findFollowingByTargetMember(targetMember, loginMember, pageRequest);
        PageInfo pageInfo = PageInfo.from(followingList);

        List<FollowMemberDetailResponseDTO> followingMemberResponses = followingList.getContent().stream()
                .map(followWithIsFollow ->
                        FollowMemberDetailResponseDTO.of(followWithIsFollow.getFollow().getFollowingMember(), followWithIsFollow.getStatus()))
                .toList();

        return FollowMembersResponseDTO.of(followingMemberResponses, pageInfo);
    }

    public FollowMembersResponseDTO getFollowerList(Optional<String> loginUsername, String targetUsername, PageRequest pageRequest) {
        Member targetMember = memberRepository.findByUsername(targetUsername).orElseThrow(NotFoundMemberException::new);
        Member loginMember = loginUsername.map(s -> memberRepository.findByUsername(s)
                .orElseThrow(NotFoundMemberException::new))
                .orElse(null);

        Page<FollowWithIsFollow> followerList = followRepository.findFollowerByTargetMember(targetMember, loginMember, pageRequest);
        PageInfo pageInfo = PageInfo.from(followerList);

        List<FollowMemberDetailResponseDTO> followerMemberResponses = followerList.getContent().stream()
                .map(followWithIsFollow ->
                        FollowMemberDetailResponseDTO.of(followWithIsFollow.getFollow().getFollower(), followWithIsFollow.getStatus()))
                .toList();

        return FollowMembersResponseDTO.of(followerMemberResponses, pageInfo);
    }
}
