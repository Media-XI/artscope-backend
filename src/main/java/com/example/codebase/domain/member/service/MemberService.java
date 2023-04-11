package com.example.codebase.domain.member.service;

import com.example.codebase.domain.auth.OAuthAttributes;
import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.example.codebase.domain.member.dto.UpdateMemberDTO;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.exception.ExistMemberException;
import com.example.codebase.domain.member.exception.ExistsEmailException;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberAuthorityRepository memberAuthorityRepository;

    private final S3Service s3Service;

    @Autowired
    public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, MemberAuthorityRepository memberAuthorityRepository, S3Service s3Service) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.memberAuthorityRepository = memberAuthorityRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public MemberResponseDTO createMember(CreateMemberDTO member) {
        if (memberRepository.findByUsername(member.getUsername()).isPresent()) {
            throw new ExistMemberException();
        }

        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new ExistsEmailException();
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
    public MemberResponseDTO createAdmin(CreateMemberDTO member) {
        if (memberRepository.findByUsername(member.getUsername()).isPresent()) {
            throw new ExistMemberException();
        }

        Member newMember = Member.builder()
                .username(member.getUsername())
                .password(passwordEncoder.encode(member.getPassword()))
                .name(member.getName())
                .email(member.getEmail())
                .createdTime(LocalDateTime.now())
                .activated(true)
                .build();

        Set<MemberAuthority> memberAuthority = new HashSet<>();
        memberAuthority.add(MemberAuthority.builder()
                .authority(Authority.of("ROLE_USER"))
                .member(newMember)
                .build());
        memberAuthority.add(MemberAuthority.builder()
                .authority(Authority.of("ROLE_ADMIN"))
                .member(newMember)
                .build());
        newMember.setAuthorities(memberAuthority);

        return MemberResponseDTO.from(memberRepository.save(newMember));
    }

    @Transactional
    public Member createOAuthMember(OAuthAttributes oAuthAttributes) {
        // TODO: 이메일 중복 체크
        if (memberRepository.findByUsername(oAuthAttributes.getName()).isPresent()) {
            throw new ExistMemberException();
        }

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

    @Transactional
    public MemberResponseDTO createArtist(CreateArtistMemberDTO createArtistMemberDTO) {
        Member member = memberRepository
                .findByUsername(createArtistMemberDTO.getUsername())
                .orElseThrow(NotFoundMemberException::new);
        member.setArtist(createArtistMemberDTO);

        return MemberResponseDTO.from(member);
    }

    public MemberResponseDTO getProfile(String username) {
        Member member = memberRepository
                .findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);
        return MemberResponseDTO.from(member);
    }

    @Transactional
    public MemberResponseDTO updateMember(UpdateMemberDTO updateMemberDTO) {
        Member member = memberRepository
                .findByUsername(updateMemberDTO.getUsername())
                .orElseThrow(NotFoundMemberException::new);
        member.update(updateMemberDTO);

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public MemberResponseDTO updateProfile(String username, MultipartFile multipartFile) {
        Member member = memberRepository
                .findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        try {
            String fileUrl = s3Service.saveUploadFile(multipartFile);
            member.update(fileUrl);
        } catch (IOException e) {
            throw new RuntimeException("S3 Upload Error");
        }

        return MemberResponseDTO.from(member);
    }

    public void deleteMember(String username) {
        Member member = memberRepository
                .findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);
        memberRepository.delete(member);
    }
}
