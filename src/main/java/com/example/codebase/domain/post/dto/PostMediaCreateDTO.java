package com.example.codebase.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import java.awt.image.BufferedImage;

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

    @JsonIgnore
    public void setImageSize(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

}
