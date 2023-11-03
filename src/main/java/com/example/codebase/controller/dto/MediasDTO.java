package com.example.codebase.controller.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MediasDTO {

    private List<MultipartFile> medias;
}
