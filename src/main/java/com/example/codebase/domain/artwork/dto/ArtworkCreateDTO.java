package com.example.codebase.domain.artwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotNull(message = "작품명은 필수입니다.")
    private String title;

    @NotNull(message = "작품 설명은 필수입니다.")
    private String description;

    @NotNull(message = "노출 여부는 필수입니다.")
    private Boolean visible;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<MultipartFile> mediaFiles;

    private List<ArtworkMediaCreateDTO> medias;

    public void addMedia(ArtworkMediaCreateDTO media) {
        if (medias == null) {
            medias = new ArrayList<>();
        }
        medias.add(media);
    }
}
