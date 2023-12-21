package com.example.codebase.domain.Event.dto;

import com.example.codebase.domain.Event.entity.EventMedia;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMediaResponseDTO {

    private String mediaType;

    private String mediaUrl;

    public static EventMediaResponseDTO from(EventMedia eventMedia) {
        EventMediaResponseDTO dto = new EventMediaResponseDTO();
        dto.setMediaType(eventMedia.getEventMediaType().name());
        dto.setMediaUrl(eventMedia.getMediaUrl());
        return dto;
    }

    public static EventMediaResponseDTO from(String mediaUrl) {
        EventMediaResponseDTO dto = new EventMediaResponseDTO();
        dto.setMediaUrl(mediaUrl);
        return dto;
    }

}
