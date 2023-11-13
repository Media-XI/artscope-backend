package com.example.codebase.domain.exhibition.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ExhibitionMediaCreateDTO {

    @NotNull
    private String mediaType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mediaUrl;
}
