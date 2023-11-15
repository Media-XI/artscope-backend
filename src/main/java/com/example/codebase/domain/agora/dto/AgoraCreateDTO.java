package com.example.codebase.domain.agora.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AgoraCreateDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "동의에 대한 문구는 필수입니다.")
    private String agreeText;

    @NotBlank(message = "중립에 대한 문구는 필수입니다.")
    private String naturalText;

    @NotBlank(message = "반대에 대한 문구는 필수입니다.")
    private String disagreeText;

    @NotNull(message = "익명 여부는 필수입니다.")
    private Boolean isAnonymous;

    @Valid
    private List<AgoraMediaCreateDTO> medias;

    @Valid
    private AgoraMediaCreateDTO thumbnail;

}
