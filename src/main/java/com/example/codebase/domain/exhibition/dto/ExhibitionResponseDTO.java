package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.document.ExhibitionDocument;
import com.example.codebase.domain.exhibition.entity.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionResponseDTO {

    private Long id;

    private String title;

    private String author;

    private ExhibitionMediaResponseDTO thumbnail;

    private EventType eventType;

    private EventScheduleResponseDTO eventSchedule;

    private String location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static ExhibitionResponseDTO from(ExhibitionWithEventSchedule exhibitionWithEventSchedule) {
        Exhibition exhibition = exhibitionWithEventSchedule.getExhibition();
        EventSchedule eventSchedule = exhibitionWithEventSchedule.getEventSchedule();

        List<ExhibitionMedia> medias = exhibition.getExhibitionMedias();

        // 썸네일
        ExhibitionMediaResponseDTO thumbnail =
                medias.stream().findFirst().map(ExhibitionMediaResponseDTO::from).orElse(null);

        // 해당 조회한 스케쥴
        EventScheduleResponseDTO eventScheduleDTO = EventScheduleResponseDTO.from(eventSchedule);

        return ExhibitionResponseDTO.builder()
                .id(exhibition.getId())
                .title(exhibition.getTitle())
                .author(exhibition.getMember().getName())
                .thumbnail(thumbnail)
                .eventSchedule(eventScheduleDTO)
                .eventType(exhibition.getType())
                .location(eventSchedule.getLocation().getAddress())
                .createdTime(exhibition.getCreatedTime())
                .updatedTime(exhibition.getUpdatedTime())
                .build();
    }

    public static ExhibitionResponseDTO from(ExhibitionDocument exhibitionDocument) {

        ExhibitionMediaResponseDTO media = new ExhibitionMediaResponseDTO();
        if (exhibitionDocument.getMediaUrl() != null) {
            media.setMediaUrl(exhibitionDocument.getMediaUrl());
        }

        return ExhibitionResponseDTO.builder()
                .id(exhibitionDocument.getId())
                .title(exhibitionDocument.getTitle())
                .author(exhibitionDocument.getName())
                .thumbnail(media)
                .createdTime(exhibitionDocument.getCreatedTime())
                .updatedTime(exhibitionDocument.getUpdatedTime())
                .build();
    }


}
