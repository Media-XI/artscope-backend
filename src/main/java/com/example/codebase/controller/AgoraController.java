package com.example.codebase.controller;

import com.example.codebase.domain.agora.dto.*;
import com.example.codebase.domain.agora.service.AgoraService;
import com.example.codebase.domain.image.service.ImageService;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/agoras")
@ApiOperation(value = "아고라", notes = "")
public class AgoraController {

    private final ImageService imageService;

    private final AgoraService agoraService;

    @Autowired
    public AgoraController(ImageService imageService, AgoraService agoraService) {
        this.imageService = imageService;
        this.agoraService = agoraService;
    }

    @ApiOperation(value = "아고라 생성", notes = "아고라를 생성합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createAgora(
        @RequestPart(value = "dto") @Valid AgoraCreateDTO dto,
        @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
        @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile
    ) throws IOException {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);

        imageService.uploadMedias(dto, mediaFiles);
        imageService.uploadThumbnail(dto, thumbnailFile);

        AgoraResponseDTO agora = agoraService.createAgora(dto, username);

        return new ResponseEntity(agora, HttpStatus.CREATED);
    }

    @ApiOperation(value = "아고라 전체 조회", notes = "[페이지네이션] 아고라를 전체 조회합니다.")
    @GetMapping
    public ResponseEntity getAllAgora(
        @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        AgorasResponseDTO agoras = agoraService.getAllAgora(pageRequest);
        return new ResponseEntity(agoras, HttpStatus.OK);
    }

    @ApiOperation(value = "아고라 상세 조회", notes = "아고라를 상세 조회합니다.")
    @GetMapping("/{agoraId}")
    public ResponseEntity getAgora(
        @PathVariable Long agoraId
    ) {
        String username = SecurityUtil.getCurrentUsername().orElse(null);
        AgoraDetailReponseDTO agora = agoraService.getAgora(agoraId, username);
        return new ResponseEntity(agora, HttpStatus.OK);
    }

    @ApiOperation(value = "아고라 수정", notes = "아고라를 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{agoraId}")
    public ResponseEntity updateAgora(
        @PathVariable Long agoraId,
        @RequestBody @Valid AgoraUpdateDTO dto
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);

        AgoraResponseDTO agora = agoraService.updateAgora(agoraId, dto, username);
        return new ResponseEntity(agora, HttpStatus.OK);
    }

    @ApiOperation(value = "아고라 삭제", notes = "아고라를 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{agoraId}")
    public ResponseEntity deleteAgora(
        @PathVariable Long agoraId
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        agoraService.deleteAgora(agoraId, username);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "아고라 투표", notes = "아고라 투표를 합니다. \n 해당 투표 내용으로 찬성, 반대를 결정합니다. \n 이미 투표한 사람이 동일한 투표 내용으로 투표하면 취소됩니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{agoraId}/vote")
    public ResponseEntity voteAgora(
        @PathVariable Long agoraId,
        @RequestBody @NotBlank(message = "투표 내용을 작성해주세요.") String vote
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        AgoraResponseDTO agora = agoraService.voteAgora(agoraId, vote, username);

        if (agora.isUserVoteCancle()) {
            return new ResponseEntity(agora, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(agora, HttpStatus.OK);
    }

    @ApiOperation(value = "아고라 의견 생성", notes = "아고라에 의견을 생성합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{agoraId}/opinions")
    public ResponseEntity createOpinion(
        @PathVariable Long agoraId,
        @RequestBody @NotBlank(message = "의견 내용을 작성해주세요.") AgoraOpinionRequestDTO content
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        AgoraDetailReponseDTO opinion = agoraService.createOpinion(agoraId, content, username);
        return new ResponseEntity(opinion, HttpStatus.CREATED);
    }

    @ApiOperation(value = "아고라 의견 수정", notes = "해당 의견을 생성합니다.")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{agoraId}/opinions/{opinionId}")
    public ResponseEntity updateOpinion(
        @PathVariable Long agoraId,
        @PathVariable Long opinionId,
        @RequestBody @NotBlank(message = "수정할 의견 내용을 작성해주세요.") AgoraOpinionRequestDTO content
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        AgoraDetailReponseDTO opinion = agoraService.updateOpinion(agoraId, opinionId, content, username);
        return new ResponseEntity(opinion, HttpStatus.OK);
    }

    @ApiOperation(value = "아고라 의견 삭제", notes = "해당 의견을 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{agoraId}/opinions/{opinionId}")
    public ResponseEntity updateOpinion(
        @PathVariable Long agoraId,
        @PathVariable Long opinionId
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        AgoraDetailReponseDTO opinion = agoraService.deleteOpinion(agoraId, opinionId, username, SecurityUtil.isAdmin());
        return new ResponseEntity(opinion, HttpStatus.NO_CONTENT);
    }

}
