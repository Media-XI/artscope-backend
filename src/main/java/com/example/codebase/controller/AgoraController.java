package com.example.codebase.controller;

import com.example.codebase.domain.agora.dto.*;
import com.example.codebase.domain.agora.service.AgoraService;
import com.example.codebase.domain.image.service.ImageService;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/agoras")
@Tag(name = "Agora", description = "아고라 API")
public class AgoraController {

    private final ImageService imageService;

    private final AgoraService agoraService;

    @Autowired
    public AgoraController(ImageService imageService, AgoraService agoraService) {
        this.imageService = imageService;
        this.agoraService = agoraService;
    }

    @Operation(summary = "아고라 생성", description = "아고라를 생성합니다.")
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

    @Operation(summary = "아고라 목록 조회", description = "아고라 목록을 조회합니다.")
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

    @Operation(summary = "아고라 상세 조회", description = "아고라 상세를 조회합니다.")
    @GetMapping("/{agoraId}")
    public ResponseEntity getAgora(
        @PathVariable Long agoraId
    ) {
        String username = SecurityUtil.getCurrentUsername().orElse(null);
        AgoraDetailReponseDTO agora = agoraService.getAgora(agoraId, username);
        return new ResponseEntity(agora, HttpStatus.OK);
    }

    @Operation(summary = "아고라 수정", description = "아고라를 수정합니다.")
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

    @Operation(summary = "아고라 삭제", description = "아고라를 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{agoraId}")
    public ResponseEntity deleteAgora(
        @PathVariable Long agoraId
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        agoraService.deleteAgora(agoraId, username);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "아고라 투표", description = "아고라에 투표합니다.")
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

    @Operation(summary = "아고라 의견 생성", description = "아고라 의견을 생성합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{agoraId}/opinions")
    public ResponseEntity createOpinion(
        @PathVariable Long agoraId,
        @RequestBody @Valid AgoraOpinionRequestDTO content
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        AgoraDetailReponseDTO opinion = agoraService.createOpinion(agoraId, content, username);
        return new ResponseEntity(opinion, HttpStatus.CREATED);
    }

    @Operation(summary = "아고라 의견 수정", description = "아고라 의견을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{agoraId}/opinions/{opinionId}")
    public ResponseEntity updateOpinion(
        @PathVariable Long agoraId,
        @PathVariable Long opinionId,
        @RequestBody @Valid AgoraOpinionRequestDTO content
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        AgoraDetailReponseDTO opinion = agoraService.updateOpinion(agoraId, opinionId, content, username);
        return new ResponseEntity(opinion, HttpStatus.OK);
    }

    @Operation(summary = "아고라 의견 삭제", description = "아고라 의견을 삭제합니다.")
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
