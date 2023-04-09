package com.example.codebase.domain.artwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
public class ArtworkMediaCreateDTO {
    @NotNull
    private String mediaType;


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mediaUrl;

    @Null
    private String description;
}
