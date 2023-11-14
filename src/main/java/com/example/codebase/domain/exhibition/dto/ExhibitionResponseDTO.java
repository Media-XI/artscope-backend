package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.example.codebase.domain.exhibition.entity.EventType;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static ExhibitionResponseDTO from(Exhibition exhibition) {
        List<ExhibitionMedia> medias = exhibition.getExhibitionMedias();

        // 썸네일
        ExhibitionMediaResponseDTO thumbnail =
                medias.stream().findFirst().map(ExhibitionMediaResponseDTO::from).orElse(null);

        // 제일 처음 일정 가져옴
        EventSchedule eventSchedule = exhibition.getEventSchedules().stream().findFirst().orElse(null);
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


}
