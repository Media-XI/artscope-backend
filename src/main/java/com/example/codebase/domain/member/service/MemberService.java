package com.example.codebase.domain.member.service;

import com.example.codebase.domain.auth.OAuthAttributes;
import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.exception.ExistMemberException;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, MemberAuthorityRepository memberAuthorityRepository) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.memberAuthorityRepository = memberAuthorityRepository;
    }

    @Transactional
    public MemberResponseDTO createMember(CreateMemberDTO member) {
        if (memberRepository.findByUsername(member.getUsername()).isPresent()) {
            throw new ExistMemberException();
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        Member newMember = Member.builder()
                .username(member.getUsername())
                .password(passwordEncoder.encode(member.getPassword()))
                .name(member.getName())
                .email(member.getEmail())
                .createdTime(LocalDateTime.now())
                .activated(true)
                .build();

        MemberAuthority memberAuthority = MemberAuthority.builder()
                .authority(authority)
                .member(newMember)
                .build();
        newMember.setAuthorities(Collections.singleton(memberAuthority));

        Member save = memberRepository.save(newMember);
        memberAuthorityRepository.save(memberAuthority);

        return MemberResponseDTO.from(save);
    }

    @Transactional
    public Member createOAuthMember(OAuthAttributes oAuthAttributes) {
        // New Save
        Authority authority = new Authority();
        authority.setAuthorityName("ROLE_USER");

        Member newMember = oAuthAttributes.toEntity(passwordEncoder);
        MemberAuthority memberAuthority = MemberAuthority.builder()
                .authority(authority)
                .member(newMember)
                .build();
        newMember.setAuthorities(Collections.singleton(memberAuthority));

        Member save = memberRepository.save(newMember);
        memberAuthorityRepository.save(memberAuthority);

        return save;
    }

    public List<MemberResponseDTO> getAllMember() {
        return memberRepository.findAll().stream()
                .map(MemberResponseDTO::from)
                .collect(Collectors.toList());
    }

    public MemberResponseDTO createArtist(CreateArtistMemberDTO createArtistMemberDTO) {
        Member member = memberRepository
                .findByUsername(createArtistMemberDTO.getUsername())
                .orElseThrow(NotFoundMemberException::new);
        member.setArtist(createArtistMemberDTO);

        Member saved = memberRepository.save(member);
        return MemberResponseDTO.from(saved);
    }

    public MemberResponseDTO getProfile(String username) {
        Member member = memberRepository
                .findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);
        return MemberResponseDTO.from(member);
    }
}
