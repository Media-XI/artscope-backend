package com.example.codebase.domain.artwork.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
