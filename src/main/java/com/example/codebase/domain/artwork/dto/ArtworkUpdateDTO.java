package com.example.codebase.domain.artwork.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ArtworkUpdateDTO {

    @NotNull
    private String title;

    private List<String> tags;

    @NotNull
    private String description;

    @NotNull
    private boolean visible;
}
