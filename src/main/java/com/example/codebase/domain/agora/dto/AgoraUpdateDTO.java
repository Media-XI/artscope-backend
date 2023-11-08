package com.example.codebase.domain.agora.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgoraUpdateDTO {

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

}
