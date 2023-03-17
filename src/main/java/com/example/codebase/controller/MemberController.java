package com.example.codebase.controller;

import com.example.codebase.domain.member.dto.MemberDTO;
import com.example.codebase.domain.member.dto.MemberResponseDTO;
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
@ApiOperation(value = "회원 관련 APIs" , notes = "")
@RequestMapping("/api/member")
public class MemberController {

    private MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @ApiOperation(value = "회원 가입", notes = "회원 가입을 합니다.")
    @PostMapping("")
    public ResponseEntity createMember(@RequestBody MemberDTO memberDTO) {
        try {
            MemberResponseDTO memberResponseDTO = memberService.createMember(memberDTO);
            return new ResponseEntity(memberResponseDTO, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @ApiOperation(value = "전체 회원 조회" , notes = "등록된 전체 회원을 조회합니다.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity getAllMember() {
        List<Member> members;
        try {
            members = memberService.getAllMember();
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(members, HttpStatus.OK);
    }

}
