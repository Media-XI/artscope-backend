package com.example.codebase.controller;

import io.swagger.annotations.ApiOperation;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation(value = "전체 회원 조회" , notes = "등록된 전체 회원을 조회합니다.")
    @GetMapping("/")
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
