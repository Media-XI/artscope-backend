package com.example.codebase.domain.agoda.dto;

import com.example.codebase.domain.agoda.entity.AgoraMedia;
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
public class AgoraMediaResponseDTO {

    private String mediaType;

    private String mediaUrl;

    private Integer imageWidth;

    private Integer imageHeight;

    public static AgoraMediaResponseDTO from(AgoraMedia media) {
        return AgoraMediaResponseDTO.builder()
                .mediaType(media.getMediaType().name())
                .mediaUrl(media.getMediaUrl())
                .imageWidth(media.getMediaWidth())
                .imageHeight(media.getMediaHeight())
                .build();
    }

}
