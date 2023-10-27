package com.example.codebase.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.image.BufferedImage;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostMediaCreateDTO {

    @NotBlank
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
