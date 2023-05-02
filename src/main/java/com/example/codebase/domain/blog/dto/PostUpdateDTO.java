package com.example.codebase.domain.blog.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDTO {

    private String title;

    private String content;
}

