package com.example.codebase.controller;

import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ApiOperation(value = "회원", notes = "회원 관련 API")
@RequestMapping("/api/member")
public class MemberController {

    private MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @ApiOperation(value = "회원 가입", notes = "회원 가입을 합니다.")
    @PostMapping("")
    public ResponseEntity createMember(@RequestBody CreateMemberDTO createMemberDTO) {
        MemberResponseDTO memberResponseDTO = memberService.createMember(createMemberDTO);
        return new ResponseEntity(memberResponseDTO, HttpStatus.CREATED);
    }

    @ApiOperation(value = "아티스트 정보 입력", notes = "[USER] 아티스트 정보를 입력합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping("/artist")
    public ResponseEntity createArtist(@RequestBody CreateArtistMemberDTO createArtistMemberDTO) {
        SecurityUtil.getCurrentUsername().ifPresent(createArtistMemberDTO::setUsername);
        MemberResponseDTO artist = memberService.createArtist(createArtistMemberDTO);
        return new ResponseEntity(artist, HttpStatus.CREATED);
    }


    @ApiOperation(value = "전체 회원 조회", notes = "[ADMIN] 등록된 전체 회원을 조회합니다.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity getAllMember() {
        List<MemberResponseDTO> members = memberService.getAllMember();
        return new ResponseEntity(members, HttpStatus.OK);
    }
}
