package com.example.codebase.controller;

import com.example.codebase.domain.agoda.dto.AgoraCreateDTO;
import com.example.codebase.domain.agoda.dto.AgoraReponseDTO;
import com.example.codebase.domain.agoda.service.AgoraService;
import com.example.codebase.domain.image.service.ImageService;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

        imageService.mediasUpload(dto, mediaFiles);
        imageService.thumbnailUpload(dto, thumbnailFile);

        AgoraReponseDTO agora = agoraService.createAgora(dto, username);

        return new ResponseEntity(agora, HttpStatus.CREATED);
    }
}
