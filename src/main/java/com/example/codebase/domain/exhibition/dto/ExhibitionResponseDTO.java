package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventType;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionResponseDTO {
  private Long id;

  private String title;

  private String author;

  private String description;

  private ExhibitionMediaResponseDTO thumbnail;

  private List<ExhibitionMediaResponseDTO> medias;

  private String link;

  private EventType eventType;

  private List<EventScheduleResponseDTO> eventSchedule;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedTime;

  public static ExhibitionResponseDTO from(Exhibition exhibition) {
    List<ExhibitionMedia> medias = exhibition.getExhibitionMedias();

    // 썸네일
    ExhibitionMediaResponseDTO thumbnail =
        medias.stream().findFirst().map(ExhibitionMediaResponseDTO::from).orElse(null);

    // 미디어
    List<ExhibitionMediaResponseDTO> exhibitionMediaResponseDTOS =
        medias.stream().skip(1).map(ExhibitionMediaResponseDTO::from).collect(Collectors.toList());

    List<EventScheduleResponseDTO> eventScheduleDTOS =
        exhibition.getEventSchedules().stream()
            .map(EventScheduleResponseDTO::from)
            .collect(Collectors.toList());

    return ExhibitionResponseDTO.builder()
        .id(exhibition.getId())
        .title(exhibition.getTitle())
        .author(exhibition.getMember().getName())
        .description(exhibition.getDescription())
        .thumbnail(thumbnail)
        .medias(exhibitionMediaResponseDTOS)
        .eventSchedule(eventScheduleDTOS)
        .link(exhibition.getLink())
        .eventType(exhibition.getType())
        .createdTime(exhibition.getCreatedTime())
        .updatedTime(exhibition.getUpdatedTime())
        .build();
  }
}
