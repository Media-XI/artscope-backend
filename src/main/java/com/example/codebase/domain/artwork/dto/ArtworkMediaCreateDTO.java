package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.MediaType;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
public class ArtworkMediaCreateDTO {
    @NotNull
    private String mediaType;

    @NotNull
    private String mediaUrl;

    @Null
    private String description;
}
