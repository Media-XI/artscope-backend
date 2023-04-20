package com.example.codebase.domain.artwork.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
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
