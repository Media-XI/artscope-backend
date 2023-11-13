package com.example.codebase.domain.agora.dto;

import com.example.codebase.domain.agora.entity.AgoraMedia;
import lombok.*;

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
