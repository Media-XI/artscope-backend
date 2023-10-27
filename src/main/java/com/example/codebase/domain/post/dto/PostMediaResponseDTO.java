package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.post.entity.PostMedia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMediaResponseDTO {
    private Long id;

    private String mediaType;

    private String mediaUrl;

    private Integer mediaidth;

    private Integer mediaHeight;

    public static PostMediaResponseDTO from(PostMedia postMedia) {
        return PostMediaResponseDTO.builder()
                .id(postMedia.getId())
                .mediaUrl(postMedia.getMediaUrl())
                .mediaType(postMedia.getMediaType().name())
                .mediaidth(postMedia.getMediaWidth())
                .mediaHeight(postMedia.getMediaHeight())
                .build();
    }


}
