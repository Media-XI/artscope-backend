package com.example.codebase.domain.post.dto;


import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentCreateDTO {

    @NotEmpty
    private String content;

    private Long parentCommentId;

}
