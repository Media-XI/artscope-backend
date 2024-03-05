package com.example.codebase.controller;

import com.example.codebase.annotation.AdminOnly;
import com.example.codebase.domain.curation.dto.CurationRequest;
import com.example.codebase.domain.curation.dto.CurationResponse;
import com.example.codebase.domain.curation.dto.CurationTime;
import com.example.codebase.domain.curation.service.CurationService;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "큐레이션 API", description = "큐레이션 관련 API")
@RequestMapping("/api/curations")
public class CurationController {

    private final CurationService curationService;

    private final MemberService memberService;

    @Autowired
    public CurationController(CurationService curationService, MemberService memberService) {
        this.curationService = curationService;
        this.memberService = memberService;
    }

    @Operation(summary = "큐레이션 생성", description = "[ADMIN] 큐레이션을 생성합니다")
    @PostMapping
    @AdminOnly
    public ResponseEntity createCuration(@RequestBody @Valid CurationRequest.Create curationRequest) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow();
        memberService.getEntity(loginUsername);

        CurationResponse.GetAll curation = curationService.createCuration(curationRequest);

        return new ResponseEntity(curation, HttpStatus.CREATED);
    }

    @Operation(summary = "큐레이션 수정", description = "[ADMIN] 큐레이션을 수정합니다")
    @PatchMapping
    @AdminOnly
    public ResponseEntity updateCuration(@RequestBody CurationRequest.Update curationRequest) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow();
        memberService.getEntity(loginUsername);

        CurationResponse.Get curation = curationService.updateCuration(curationRequest);

        return new ResponseEntity(curation, HttpStatus.OK);
    }

    @Operation(summary = "큐레이션 삭제", description = "[ADMIN] 큐레이션을 삭제합니다")
    @DeleteMapping("/{curationId}")
    @AdminOnly
    public ResponseEntity deleteCuration(@PathVariable Long curationId) {

        curationService.deleteCuration(curationId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "큐레이션 전체 조회", description = "금일로 부터 일주일, 한달 이내의 큐레이션을 전체 조회합니다, (기본값 MONTH)")
    @GetMapping()
    public ResponseEntity getCuration(@RequestParam(value = "time", defaultValue = "MONTH") CurationTime time,
                                      @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
                                      @PositiveOrZero @RequestParam(value = "size", defaultValue = "22") int size,
                                      @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "updatedTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        CurationResponse.GetAll curation = curationService.getAllCuration(time, pageRequest);

        return new ResponseEntity(curation, HttpStatus.OK);
    }

}
