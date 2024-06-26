package com.example.codebase.domain.member.service;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.auth.OAuthAttributes;
import com.example.codebase.domain.member.dto.*;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.entity.RoleStatus;
import com.example.codebase.domain.member.exception.ExistMemberException;
import com.example.codebase.domain.member.exception.ExistsEmailException;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.notification.repository.NotificationSettingRepository;
import com.example.codebase.s3.S3Service;
import com.example.codebase.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;

    @Autowired
    public MemberService(
            PasswordEncoder passwordEncoder,
            MemberRepository memberRepository,
            RedisUtil redisUtil) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.redisUtil = redisUtil;
    }

    @Transactional
    public MemberResponseDTO createMember(CreateMemberDTO member) {
        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new ExistMemberException();
        }

        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new ExistsEmailException();
        }

        Authority authority = Authority.builder().authorityName("ROLE_GUEST").build();

        Member newMember = Member.create(passwordEncoder, member);

        MemberAuthority memberAuthority =
                MemberAuthority.builder().authority(authority).member(newMember).build();
        newMember.addAuthority(memberAuthority);

        NotificationSetting notificationSetting = NotificationSetting.builder().member(newMember).build();
        newMember.setNotificationSetting(notificationSetting);

        Member save = memberRepository.save(newMember);
        return MemberResponseDTO.from(save);
    }

    @Transactional
    public MemberResponseDTO createAdmin(CreateMemberDTO member) {
        if (memberRepository.findByUsername(member.getUsername()).isPresent()) {
            throw new ExistMemberException();
        }

        Member newMember =
                Member.builder()
                        .username(member.getUsername())
                        .password(passwordEncoder.encode(member.getPassword()))
                        .name(member.getName())
                        .email(member.getEmail())
                        .createdTime(LocalDateTime.now())
                        .activated(true)
                        .build();

        MemberAuthority userAuthority =
                MemberAuthority.builder().authority(Authority.of("ROLE_USER")).member(newMember).build();
        newMember.addAuthority(userAuthority);

        MemberAuthority adminAuthority =
                MemberAuthority.builder().authority(Authority.of("ROLE_ADMIN")).member(newMember).build();
        newMember.addAuthority(adminAuthority);

        return MemberResponseDTO.from(memberRepository.save(newMember));
    }

    @Transactional
    public Member createOAuthMember(OAuthAttributes oAuthAttributes) {
        if (isExistUsername(oAuthAttributes.getOAuthProviderId())) {
            throw new ExistMemberException();
        }

        if (isExistEmail(oAuthAttributes.getEmail())) {
            throw new ExistsEmailException();
        }

        // New Save
        Authority authority = new Authority();
        authority.setAuthorityName("ROLE_USER");

        Member newMember = Member.from(passwordEncoder, oAuthAttributes);
        MemberAuthority memberAuthority =
                MemberAuthority.builder().authority(authority).member(newMember).build();
        newMember.addAuthority(memberAuthority);

        Member save = memberRepository.save(newMember);
        return save;
    }

    public MembersResponseDTO getAllMember(PageRequest pageRequest) {
        Page<Member> members = memberRepository.findAll(pageRequest);
        return MembersResponseDTO.from(members);
    }

    @Transactional
    public MemberResponseDTO createArtist(CreateArtistMemberDTO createArtistMemberDTO) {
        Member member =
                memberRepository
                        .findByUsername(createArtistMemberDTO.getUsername())
                        .orElseThrow(NotFoundMemberException::new);
        member.setArtist(createArtistMemberDTO);

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public MemberResponseDTO createCurator(CreateCuratorMemberDTO createCuratorMemberDTO) {
        Member member =
                memberRepository
                        .findByUsername(createCuratorMemberDTO.getUsername())
                        .orElseThrow(NotFoundMemberException::new);
        member.setCurator(createCuratorMemberDTO);

        return MemberResponseDTO.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDTO getProfile(String username) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);
        return MemberResponseDTO.from(member);
    }

    @Transactional
    public MemberResponseDTO updateMember(String username, UpdateMemberDTO updateMemberDTO) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        member.update(updateMemberDTO);

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public MemberResponseDTO updateProfile(String username, String profileUrl) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        member.update(profileUrl);

        return MemberResponseDTO.from(member);
    }

    public void deleteMember(String username) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        memberRepository.delete(member);
    }

    public boolean isExistEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean isExistUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Transactional
    public MemberResponseDTO updateRoleStatus(String username, RoleStatus roleStatus) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        member.updateRoleStatus(roleStatus);

        if (roleStatus == RoleStatus.ARTIST) {
            member.addAuthority(Authority.of("ROLE_ARTIST"));

        } else if (roleStatus == RoleStatus.CURATOR) {
            member.addAuthority(Authority.of("ROLE_CURATOR"));
        }

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public MemberResponseDTO updateUsername(String username, String newUsername) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        Boolean existsByUsername = memberRepository.existsByUsername(newUsername);
        if (existsByUsername) {
            throw new RuntimeException("사용중인 아이디입니다.");
        }

        member.updateUsername(newUsername);

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public void updatePassword(String username, String password) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        member.updatePassword(passwordEncoder.encode(password));
    }

    @Transactional
    public MemberResponseDTO updateAdmin(String username) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        MemberAuthority adminAuthority = new MemberAuthority();
        adminAuthority.setAuthority(Authority.of("ROLE_ADMIN"));
        adminAuthority.setMember(member);

        member.addAuthority(adminAuthority);

        return MemberResponseDTO.from(member);
    }

    public List<MemberSearchResponseDTO> searchMember(String username) {
        Page<Member> searchResults = null;
        Pageable pageable = PageRequest.of(0, 10);

        String regex =
                "^([\\w\\.\\_\\-])*[a-zA-Z0-9]+([\\w\\.\\_\\-])*([a-zA-Z0-9])+([\\w\\.\\_\\-])+@([a-zA-Z0-9]+\\.)+[a-zA-Z0-9]{2,8}$";
        if (username.matches(regex)) {
            searchResults = memberRepository.searchByEmail(username, pageable);
        } else if (username.startsWith("@")) {
            username = username.substring(1);
            searchResults = memberRepository.searchByUsername(username, pageable);
        } else {
            searchResults = memberRepository.searchByName(username, pageable);
        }

        return searchResults.stream().map(MemberSearchResponseDTO::from).collect(Collectors.toList());
    }

    public MembersResponseDTO getAllRoleStatusMember(String roleStatus, PageRequest pageRequest) {
        if (roleStatus.equals("PENDING") || roleStatus.equals("REJECTED")) {
            return getRoleStatusMember(roleStatus, pageRequest);
        }

        Page<Member> members = memberRepository.findAllByRoleStatus(RoleStatus.create(roleStatus), pageRequest);
        return MembersResponseDTO.from(members);
    }

    private MembersResponseDTO getRoleStatusMember(String roleStatus, PageRequest pageRequest) {
        RoleStatus[] roleStatusEnums = new RoleStatus[2];

        if (roleStatus.equals("PENDING")) {
            roleStatusEnums[0] = RoleStatus.ARTIST_PENDING;
            roleStatusEnums[1] = RoleStatus.CURATOR_PENDING;
        } else {
            roleStatusEnums[0] = RoleStatus.ARTIST_REJECTED;
            roleStatusEnums[1] = RoleStatus.CURATOR_REJECTED;
        }

        Page<Member> members = memberRepository.findAllByRoleStatus(roleStatusEnums[0], roleStatusEnums[1], pageRequest);
        return MembersResponseDTO.from(members);
    }

    @Transactional
    public void resetPassword(String code, PasswordResetRequestDTO passwordDto) {
        String email = redisUtil.getData(code)
                .orElseThrow(() -> new RuntimeException("인증 코드가 유효하지 않습니다."));

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        String memberPassword = member.getPassword();

        boolean oldPasswordMatches = passwordEncoder.matches(passwordDto.oldPassword(), memberPassword);
        if (!oldPasswordMatches) {
            throw new RuntimeException("기존 비밀번호가 일치하지 않습니다.");
        }

        boolean newPasswordMatches = passwordEncoder.matches(passwordDto.newPassword(), memberPassword);
        if (newPasswordMatches) {
            throw new RuntimeException("기존 비밀번호와 동일한 새 비밀번호입니다.");
        }

        redisUtil.deleteData(code);

        String newPasswordEncoded = passwordEncoder.encode(passwordDto.newPassword());
        member.updatePassword(newPasswordEncoded);
    }

    @Transactional
    public String updateEmailReceive(String username, boolean emailReceive) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        member.updateEmailReceive(emailReceive);

        String allow = emailReceive ? "동의" : "거부";
        return "이메일 수신 여부가 " + member.getAllowEmailReceiveDatetime() + " 기준 " + allow + "로 변경되었습니다.";
    }

    public List<String> getAllUsername() {
        return memberRepository.findAllUsername();
    }

    public Member getEntity(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);
    }

    @Transactional(readOnly = true)
    public MemberResponseDTO.TeamProfiles getTeamProfiles(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        return MemberResponseDTO.TeamProfiles.from(member.getTeamUser());
    }
}
