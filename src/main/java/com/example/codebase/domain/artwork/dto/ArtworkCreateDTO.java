package com.example.codebase.domain.artwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ArtworkCreateDTO {

    @NotBlank(message = "작품명은 필수입니다.")
    private String title;

    private List<String> tags;

    @NotBlank(message = "작품 설명은 필수입니다.")
    private String description;

    @NotBlank(message = "노출 여부는 필수입니다.")
    private Boolean visible;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<MultipartFile> mediaFiles;

    private List<ArtworkMediaCreateDTO> medias;

    private ArtworkMediaCreateDTO thumbnail;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MultipartFile thumbnailFile;

}
