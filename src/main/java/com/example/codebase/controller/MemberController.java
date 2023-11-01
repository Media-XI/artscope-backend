package com.example.codebase.controller;

import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.dto.CreateCuratorMemberDTO;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.example.codebase.domain.member.dto.UpdateMemberDTO;
import com.example.codebase.domain.member.dto.UsernameDTO;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.exception.NotAcceptTypeException;
import com.example.codebase.util.FileUtil;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@ApiOperation(value = "회원", notes = "회원 관련 API")
@Validated
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @ApiOperation(value = "회원 가입", notes = "회원 가입을 합니다.")
    @PostMapping
    public ResponseEntity createMember(@Valid @RequestBody CreateMemberDTO createMemberDTO) {
        MemberResponseDTO memberResponseDTO = memberService.createMember(createMemberDTO);
        return new ResponseEntity(memberResponseDTO, HttpStatus.CREATED);
    }

    @ApiOperation(value = "관리자 가입", notes = "관리자 가입을 합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity createAdmin(@Valid @RequestBody CreateMemberDTO createMemberDTO) {
        MemberResponseDTO admin = memberService.createAdmin(createMemberDTO);
        return new ResponseEntity(admin, HttpStatus.CREATED);
    }


    @ApiOperation(value = "아티스트 정보 입력", notes = "[USER] 아티스트 정보를 입력합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER') and !hasAnyRole('ROLE_ARTIST', 'ROLE_CURATOR')")
    @PostMapping("/artist")
    public ResponseEntity createArtist(@Valid @RequestBody CreateArtistMemberDTO createArtistMemberDTO) {
        SecurityUtil.getCurrentUsername().ifPresent(createArtistMemberDTO::setUsername);
        MemberResponseDTO artist = memberService.createArtist(createArtistMemberDTO);
        return new ResponseEntity(artist, HttpStatus.CREATED);
    }

    @ApiOperation(value = "기획자 정보 입력", notes = "[USER] 기획자 정보를 입력합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER') and !hasAnyRole('ROLE_ARTIST', 'ROLE_CURATOR')")
    @PostMapping("/curator")
    public ResponseEntity createCurator(@Valid @RequestBody CreateCuratorMemberDTO createCuratorMemberDTO) {
        SecurityUtil.getCurrentUsername().ifPresent(createCuratorMemberDTO::setUsername);
        MemberResponseDTO curator = memberService.createCurator(createCuratorMemberDTO);
        return new ResponseEntity(curator, HttpStatus.CREATED);
    }

    @ApiOperation(value = "내 정보 수정", notes = "[USER] 내 정보를 수정합니다.")
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

    @ApiOperation(value = "내 프로필 사진 수정", notes = "[USER] 내 프로필 사진을 수정합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PutMapping("/{username}/picture")
    public ResponseEntity updateProfile(
            @PathVariable String username,
            @RequestPart MultipartFile profile
    ) throws Exception {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        if (!currentUsername.equals(username)) {
            throw new RuntimeException("본인의 프로필 사진만 수정할 수 있습니다.");
        }

        String originalFilename = profile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(index + 1).toLowerCase();

        if (!FileUtil.checkImageExtension(ext)) {
            throw new NotAcceptTypeException("지원하지 않는 파일 확장자 입니다.");
        }

        MemberResponseDTO member = memberService.updateProfile(username, profile);
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @ApiOperation(value = "전체 회원 조회", notes = "[ADMIN] 등록된 전체 회원을 조회합니다.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity getAllMember() {
        List<MemberResponseDTO> members = memberService.getAllMember();
        return new ResponseEntity(members, HttpStatus.OK);
    }

    @ApiOperation(value = "프로필 조회", notes = "[USER] 로그인한 사용자의 프로필을 조회합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @GetMapping("/profile")
    public ResponseEntity getProfile() {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        MemberResponseDTO member = memberService.getProfile(username);
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @ApiOperation(value = "해당 사용자 프로필 조회", notes = "해당 사용자의 프로필을 조회합니다.")
    @GetMapping("/{username}")
    public ResponseEntity getProfile(@PathVariable String username) {
        MemberResponseDTO member = memberService.getProfile(username);
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @ApiOperation("회원 탈퇴")
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

    @ApiOperation("이메일 중복 확인")
    @GetMapping("/email/{email}")
    public ResponseEntity checkEmail(@Valid @Email @PathVariable String email) {
        if (memberService.isExistEmail(email)) {
            return new ResponseEntity("이미 존재하는 이메일 입니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("사용 가능한 이메일 입니다.", HttpStatus.OK);
    }

    @ApiOperation("아이디 중복 확인")
    @GetMapping("/username/{username}")
    public ResponseEntity checkUsername(@Valid @NotBlank @PathVariable String username) {
        if (memberService.isExistUsername(username)) {
            return new ResponseEntity("이미 존재하는 아이디 입니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("사용 가능한 아이디 입니다.", HttpStatus.OK);
    }

    @ApiOperation(value = "사용자 아티스트 상태 수정", notes = "[ADMIN] 사용자의 아티스트 상태를 수정합니다.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/artist/{username}")
    public ResponseEntity updateArtistStatus(@Valid @NotBlank @PathVariable String username,
                                             @Valid @NotBlank @RequestParam String status) {
        MemberResponseDTO member = memberService.updateArtistStatus(username, status);
        return new ResponseEntity(member, HttpStatus.OK);
    }

    @ApiOperation(value = "아이디 수정", notes = "[USER] 아이디를 수정합니다.")
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


    @ApiOperation(value = "비밀번호 변경", notes = "[로그인 사용자] 비밀번호 변경")
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

    @ApiOperation(value = "관리자 권한 부여", notes = "[관리자] 해당 사용자 관리자 권한 부여")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{username}/admin")
    public ResponseEntity updateAdmin(@PathVariable String username) {
        MemberResponseDTO member = memberService.updateAdmin(username);
        return new ResponseEntity(member, HttpStatus.OK);
    }
}
