package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.MediaType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtworkMediaCreateDTO {

    private MediaType mediaType;
    private String mediaUrl;
}
