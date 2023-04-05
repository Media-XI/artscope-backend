package com.example.codebase.domain.exhibition.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter

@Setter
public class ExhibitionMediaCreateDTO {

    @NotNull
    private String mediaType;

    @NotNull
    private String mediaUrl;

}
