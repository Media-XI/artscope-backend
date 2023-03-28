package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.MediaType;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ArtworkMediaCreateDTO {
    private String mediaType;
    private String mediaUrl;
}
