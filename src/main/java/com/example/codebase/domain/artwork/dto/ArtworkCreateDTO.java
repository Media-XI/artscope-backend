package com.example.codebase.domain.artwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArtworkCreateDTO {

    private String title;

    private String description;

    private Boolean visible;

    private List<ArtworkMediaCreateDTO> mediaUrls;

}
