package com.example.codebase.domain.artwork.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArtworkCreateDTO {

    @NotNull
    private String title;

    @Null
    private String description;

    @NotNull
    private Boolean visible;

    private List<MultipartFile> mediaFiles;

    private List<ArtworkMediaCreateDTO> medias;

    public void addMedia(ArtworkMediaCreateDTO media) {
        if (medias == null) {
            medias = new ArrayList<>();
        }
        medias.add(media);
    }
}
