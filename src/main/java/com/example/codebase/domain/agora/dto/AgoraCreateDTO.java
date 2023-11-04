package com.example.codebase.domain.agora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class AgoraCreateDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "동의에 대한 문구는 필수입니다.")
    private String agreeText;

    @NotBlank(message = "반대에 대한 문구는 필수입니다.")
    private String disagreeText;

    @NotNull(message = "익명 여부는 필수입니다.")
    private Boolean isAnonymous;

    private List<AgoraMediaCreateDTO> medias;

    // TODO: DTO 안에 Object Validation을 위한 방법을 찾아야 함
    private AgoraMediaCreateDTO thumbnail;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<MultipartFile> mediaFiles;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MultipartFile thumbnailFile;

}
