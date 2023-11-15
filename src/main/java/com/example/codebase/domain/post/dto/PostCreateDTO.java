package com.example.codebase.domain.post.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateDTO {

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private List<PostMediaCreateDTO> medias;

    private PostMediaCreateDTO thumbnail;

}
