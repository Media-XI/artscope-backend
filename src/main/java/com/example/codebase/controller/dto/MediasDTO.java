package com.example.codebase.controller.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class MediasDTO {

    private List<MultipartFile> medias;
}
