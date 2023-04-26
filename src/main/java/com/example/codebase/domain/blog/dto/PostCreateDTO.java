package com.example.codebase.domain.blog.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateDTO {

    private String title;

    private String content;

}
