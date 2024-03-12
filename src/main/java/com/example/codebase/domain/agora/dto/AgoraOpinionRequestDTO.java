package com.example.codebase.domain.agora.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgoraOpinionRequestDTO {

    @NotBlank(message = "의견 내용을 작성해주세요.")
    private String content;


}
