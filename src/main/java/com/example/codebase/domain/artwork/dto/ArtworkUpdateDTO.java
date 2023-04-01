package com.example.codebase.domain.artwork.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
public class ArtworkUpdateDTO {

    @NotNull
    private String title;

    @NotNull
    private String description;


    @NotNull
    private boolean visible;
}
