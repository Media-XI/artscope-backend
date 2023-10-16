package com.example.codebase.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateDTO {

    private String content;

    private String mentionUsername;

}
