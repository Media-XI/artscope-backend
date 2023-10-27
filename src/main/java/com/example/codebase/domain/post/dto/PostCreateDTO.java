package com.example.codebase.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<MultipartFile> mediaFiles;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MultipartFile thumbnailFile;

}
