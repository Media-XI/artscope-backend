package com.example.codebase.controller;

import com.example.codebase.domain.member.dto.ProfileUrlDTO;
import com.example.codebase.domain.member.dto.*;
import com.example.codebase.domain.member.entity.RoleStatus;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Member", description = "회원 API")
@Validated
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원 가입", description = "회원 가입을 합니다.")
    @PostMapping
    public ResponseEntity createMember(@Valid @RequestBody CreateMemberDTO createMemberDTO) {
        MemberResponseDTO memberResponseDTO = memberService.createMember(createMemberDTO);

        return new ResponseEntity(memberResponseDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "관리자 정보 입력", description = "[ADMIN] 관리자 정보를 입력합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity createAdmin(@Valid @RequestBody CreateMemberDTO createMemberDTO) {
        MemberResponseDTO admin = memberService.createAdmin(createMemberDTO);
        return new ResponseEntity(admin, HttpStatus.CREATED);
    }

    @Operation(summary = "아티스트 정보 입력", description = "[USER] 아티스트 정보를 입력합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER') and !hasAnyRole('ROLE_ARTIST', 'ROLE_CURATOR')")
    @PostMapping("/artist")
    public ResponseEntity createArtist(@Valid @RequestBody CreateArtistMemberDTO createArtistMemberDTO) {
        SecurityUtil.getCurrentUsername().ifPresent(createArtistMemberDTO::setUsername);
        MemberResponseDTO artist = memberService.createArtist(createArtistMemberDTO);
        return new ResponseEntity(artist, HttpStatus.CREATED);
    }

    @Operation(summary = "큐레이터 정보 입력", description = "[USER] 큐레이터 정보를 입력합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER') and !hasAnyRole('ROLE_ARTIST', 'ROLE_CURATOR')")
    @PostMapping("/curator")
    public ResponseEntity createCurator(@Valid @RequestBody CreateCuratorMemberDTO createCuratorMemberDTO) {
        SecurityUtil.getCurrentUsername().ifPresent(createCuratorMemberDTO::setUsername);
        MemberResponseDTO curator = memberService.createCurator(createCuratorMemberDTO);
        return new ResponseEntity(curator, HttpStatus.CREATED);
    }

    @Operation(summary = "회원 정보 수정", description = "[USER] 회원 정보를 수정합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PutMapping("/{uesrname}")
    public ResponseEntity updateMember(@PathVariable String uesrname,
                                       @Valid @RequestBody UpdateMemberDTO updateMemberDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        if (!uesrname.equals(loginUsername)) {
            throw new RuntimeException("본인의 정보만 수정할 수 있습니다.");
        }

        MemberResponseDTO member = memberService.updateMember(uesrname, updateMemberDTO);
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @Operation(summary = "프로필 사진 수정", description = "[USER] 프로필 사진을 해당 URL로 수정합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PutMapping("/{username}/picture")
    public ResponseEntity updateProfile(
            @PathVariable String username,
            @RequestBody @Valid ProfileUrlDTO urlDTO) {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        if (!currentUsername.equals(username)) {
            throw new RuntimeException("본인의 프로필 사진만 수정할 수 있습니다.");
        }

        MemberResponseDTO member = memberService.updateProfile(username, urlDTO.profile());
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @Operation(summary = "회원 전체 조회", description = "[ADMIN] 회원 리스트를 조회합니다.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity getAllMember(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        MembersResponseDTO members = memberService.getAllMember(pageRequest);
        return new ResponseEntity(members, HttpStatus.OK);
    }

    @Operation(summary = "역할 상태로 회원 전체 조회", description = "[ADMIN] 역할 상태로 회원 전체 조회합니다.", parameters = @Parameter(name = "roleStatus", description = "역할 상태", example = "NONE | PENDING | REJECTED | ARTIST | CURATOR"))
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/role-status")
    public ResponseEntity getAllRoleStatusMember(
            @RequestParam(defaultValue = "NONE") @NotBlank(message = "역할 상태는 필수입니다.") String roleStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        MembersResponseDTO members = memberService.getAllRoleStatusMember(roleStatus, pageRequest);
        return new ResponseEntity(members, HttpStatus.OK);
    }

    @Operation(summary = "회원 프로필 조회", description = "[USER] 회원의 프로필을 조회합니다.")
    @GetMapping("/{username}")
    public ResponseEntity getProfile(@PathVariable String username) {
        MemberResponseDTO member = memberService.getProfile(username);
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @Operation(summary = "회원 삭제", description = "[USER] 회원을 삭제합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @DeleteMapping("/{username}")
    public ResponseEntity deleteMember(@PathVariable String username) {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        if (!currentUsername.equals(username)) {
            throw new RuntimeException("본인의 정보만 삭제할 수 있습니다.");
        }

        memberService.deleteMember(username);
        return new ResponseEntity("성공적으로 삭제되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 확인")
    @GetMapping("/email/{email}")
    public ResponseEntity checkEmail(@Valid @Email @PathVariable String email) {
        if (memberService.isExistEmail(email)) {
            return new ResponseEntity("이미 존재하는 이메일 입니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("사용 가능한 이메일 입니다.", HttpStatus.OK);
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 확인")
    @GetMapping("/username/{username}")
    public ResponseEntity checkUsername(@Valid @NotBlank @PathVariable String username) {
        if (memberService.isExistUsername(username)) {
            return new ResponseEntity("이미 존재하는 아이디 입니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("사용 가능한 아이디 입니다.", HttpStatus.OK);
    }

    @Operation(summary = "회원 역할 상태 변경", description = "[ADMIN] 해당 회원의 역할 상태를 변경합니다.", parameters = @Parameter(name = "roleStatus", description = "역할 상태", example = "NONE | ARTIST_PENDING | ARTIST_REJECTED | ARTIST | CURATOR_PENDING | CURATOR_REJECTED | CURATOR"))
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{username}/role-status")
    public ResponseEntity updateRoleStatus(@PathVariable @Valid @NotBlank String username,
                                           @RequestParam @Valid @NotBlank(message = "역할 상태는 필수입니다.") String roleStatus) {
        MemberResponseDTO member = memberService.updateRoleStatus(username, RoleStatus.create(roleStatus));
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @Operation(summary = "닉네임 변경", description = "[USER] 닉네임을 변경합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PutMapping("/{username}/username")
    public ResponseEntity updateUsername(@PathVariable String username,
                                         @Valid @RequestParam UsernameDTO newUsername) {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        if (!SecurityUtil.isAdmin() && !currentUsername.equals(username)) { // 관리자가 아니고, 본인의 아이디가 아닐 경우
            throw new RuntimeException("본인의 정보만 수정할 수 있습니다.");
        }
        MemberResponseDTO member = memberService.updateUsername(username, newUsername.getUsername());
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 변경", description = "[USER] 비밀번호를 변경합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/{username}/password")
    public ResponseEntity updatePassword(@PathVariable String username,
                                         @NotBlank @RequestParam(value = "newPassword") String newPassword) {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        if (!SecurityUtil.isAdmin() && !currentUsername.equals(username)) {
            throw new RuntimeException("본인의 정보만 수정할 수 있습니다.");
        }

        memberService.updatePassword(username, newPassword);
        return new ResponseEntity("비밀번호가 변경되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "관리자 권한 부여", description = "[ADMIN] 관리자 권한을 부여합니다.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{username}/admin")
    public ResponseEntity updateAdmin(@PathVariable String username) {
        MemberResponseDTO member = memberService.updateAdmin(username);
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @Operation(summary = "회원 검색", description = "[ADMIN] 회원을 검색합니다.")
    @GetMapping("/search/{username}")
    public ResponseEntity searchMember(@PathVariable String username) {

        List<MemberSearchResponseDTO> memberList = memberService.searchMember(username);
        return new ResponseEntity(memberList, HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정", description = "비밀번호 재설정 합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam @NotBlank String code,
                                        @RequestBody PasswordResetRequestDTO password) {

        memberService.resetPassword(code, password);

        return new ResponseEntity("비밀번호가 재설정 되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "이메일 수신 여부 변경", description = "[USER] 이메일 수신 여부를 변경합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PutMapping("/{username}/email-receive")
    public ResponseEntity updateEmailReceive(@PathVariable String username,
                                             @RequestParam boolean emailReceive) {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        if (!currentUsername.equals(username)) {
            throw new RuntimeException("본인의 정보만 수정할 수 있습니다.");
        }

        String message = memberService.updateEmailReceive(username, emailReceive);
        return new ResponseEntity(message, HttpStatus.OK);
    }


    @Operation(summary = "회원 아이디 전체 조회", description = "검색엔진 노출 용 회원 아이디 리스트를 조회합니다.")
    @GetMapping("/username")
    public ResponseEntity getAllUsername() {
        List<String> usernameList = memberService.getAllUsername();
        return new ResponseEntity(usernameList, HttpStatus.OK);
    }

    @GetMapping("/{username}/teams")
    @Operation(summary = "회원이 속한 팀 목록 조회", description = "회원이 속한 모든 팀들을 조회합니다.")
    public ResponseEntity getTeamsByUsername(@PathVariable String username) {
        MemberResponseDTO.TeamProfiles response = memberService.getTeamProfiles(username);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
