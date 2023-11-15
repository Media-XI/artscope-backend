package com.example.codebase.domain.artwork.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class ArtworkCreateDTO {

    @NotBlank(message = "작품명은 필수입니다.")
    private String title;

    private List<String> tags;

    @NotBlank(message = "작품 설명은 필수입니다.")
    private String description;

    @NotBlank(message = "노출 여부는 필수입니다.")
    private Boolean visible;

    private List<ArtworkMediaCreateDTO> medias;

    private ArtworkMediaCreateDTO thumbnail;

}
