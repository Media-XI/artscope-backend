package com.example.codebase.domain.follow.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.follow.dto.FollowDetailResponseDTO;
import com.example.codebase.domain.follow.dto.FollowResponseDTO;
import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.entity.FollowWithIsFollow;
import com.example.codebase.domain.follow.repository.FollowRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.repository.TeamRepository;
import com.example.codebase.exception.DuplicatedRequestException;
import com.example.codebase.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Transactional(readOnly = true)
@Service
public class FollowService {

    private final FollowRepository followRepository;

    private final MemberRepository memberRepository;

    private final TeamRepository teamRepository;

    private final RedisUtil redisUtil;

    @Autowired
    public FollowService(FollowRepository followRepository, MemberRepository memberRepository, TeamRepository teamRepository, RedisUtil redisUtil) {
        this.followRepository = followRepository;
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.redisUtil = redisUtil;
    }

    @Transactional
    public void followMember(String username, String followMember) {
        checkDuplicatedRequest(username, followMember);

        Member follower = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Member following = memberRepository.findByUsername(followMember).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        if (followRepository.existsByFollowerAndFollowingMember(follower, following)) {
            throw new RuntimeException("이미 팔로잉 중입니다.");
        }

        followRepository.save(Follow.of(follower, following));
    }

    @Transactional
    public void unfollowMember(String username, String followMember) {
        checkDuplicatedRequest(username, followMember);

        Member follower = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Member following = memberRepository.findByUsername(followMember).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        Optional<Follow> alreadyFollow = followRepository.findByFollowerAndFollowingMember(follower, following);
        if (alreadyFollow.isEmpty()) {
            throw new RuntimeException("팔로잉 중이 아닙니다.");
        }
        followRepository.delete(alreadyFollow.get());
    }

    @Transactional
    public void followTeam(String username, String teamId) {
        checkDuplicatedRequest(username, teamId);

        Member follower = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Team team = teamRepository.findById(Long.valueOf(teamId)).orElseThrow(() -> new RuntimeException("존재하지 않는 팀입니다."));

        if (followRepository.existsByFollowerAndFollowingTeam(follower, team)) {
            throw new RuntimeException("이미 팔로잉 중입니다.");
        }

        followRepository.save(Follow.of(follower, team));
    }

    @Transactional
    public void unfollowTeam(String username, String teamId) {
        checkDuplicatedRequest(username, teamId);

        Member follower = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Team team = teamRepository.findById(Long.valueOf(teamId)).orElseThrow(() -> new RuntimeException("존재하지 않는 팀입니다."));

        Optional<Follow> alreadyFollow = followRepository.findByFollowerAndFollowingTeam(follower, team);
        if (alreadyFollow.isEmpty()) {
            throw new RuntimeException("팔로잉 중이 아닙니다.");
        }
        followRepository.delete(alreadyFollow.get());
    }

    // TODO : AOP 로 분리하기
    private void checkDuplicatedRequest(String follower, String following) {
        if (redisUtil.getData(follower + "->" + following).isPresent()) {
            throw new DuplicatedRequestException();
        }
        redisUtil.setDataAndExpire(follower + "->" + following, String.valueOf(LocalDateTime.now()), 3000);
    }

    public FollowResponseDTO getFollowingList(Optional<String> loginUsername, String targetUsername, PageRequest pageRequest) {
        Member targetMember = memberRepository.findByUsername(targetUsername).orElseThrow(NotFoundMemberException::new);
        Member loginMember = loginUsername.map(s -> memberRepository.findByUsername(s)
                        .orElseThrow(NotFoundMemberException::new)).orElse(null);

        Page<FollowWithIsFollow> followingList = followRepository.findFollowingByTargetMember(targetMember, loginMember, pageRequest);
        PageInfo pageInfo = PageInfo.from(followingList);

        List<FollowDetailResponseDTO> followingMemberResponses = followingList.getContent().stream()
                .map(this::getFollowMemberDetailResponseDTO)
                .toList();

        return FollowResponseDTO.of(followingMemberResponses, pageInfo);
    }

    public FollowResponseDTO getFollowerList(Optional<String> loginUsername, String targetUsername, PageRequest pageRequest) {
        Member targetMember = memberRepository.findByUsername(targetUsername).orElseThrow(NotFoundMemberException::new);
        Member loginMember = loginUsername.map(s -> memberRepository.findByUsername(s)
                        .orElseThrow(NotFoundMemberException::new)).orElse(null);

        Page<FollowWithIsFollow> followerList = followRepository.findFollowerByTargetMember(targetMember, loginMember, pageRequest);
        PageInfo pageInfo = PageInfo.from(followerList);

        List<FollowDetailResponseDTO> followerMemberResponses = followerList.getContent().stream()
                .map(this::getFollowMemberDetailResponseDTO)
                .toList();

        return FollowResponseDTO.of(followerMemberResponses, pageInfo);
    }

    private FollowDetailResponseDTO getFollowMemberDetailResponseDTO(FollowWithIsFollow followWithIsFollow) {
        Follow follow = followWithIsFollow.getFollow();
        // 팔로잉 대상이 사용자인 경우
        if (follow.getFollowingMember() != null) {
            return FollowDetailResponseDTO.of(follow.getFollowingMember(), followWithIsFollow.getStatus());
        }
        return FollowDetailResponseDTO.of(follow.getFollowingTeam(), followWithIsFollow.getStatus());
    }
}
