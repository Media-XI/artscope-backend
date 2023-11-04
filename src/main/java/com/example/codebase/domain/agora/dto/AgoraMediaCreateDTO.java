package com.example.codebase.domain.agora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.awt.image.BufferedImage;

@Getter
@Setter
public class AgoraMediaCreateDTO { // TODO : MediaCreateDTO는 중복으로 상위 클래스로 빼도 될듯

    @NotBlank(message = "미디어 타입을 입력해주세요")
    private String mediaType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mediaUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int width;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int height;

    public void setImageSize(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
}
