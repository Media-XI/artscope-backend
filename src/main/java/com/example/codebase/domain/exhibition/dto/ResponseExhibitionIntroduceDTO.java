package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.location.dto.ResponseLocationDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
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
public class ResponseExhibitionIntroduceDTO {

  ResponseExhibitionDTO exhibitionList;

  private String detailLocation;

  private int price;

  private ResponseLocationDTO location;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedTime;

  public static ResponseExhibitionIntroduceDTO from(Exhibition exhibition) {
    ResponseExhibitionDTO exhibitionList = ResponseExhibitionDTO.from(exhibition);

    ResponseLocationDTO locationDTO =
        ResponseLocationDTO.from(exhibition.getEventSchedules().get(0));

    return ResponseExhibitionIntroduceDTO.builder()
        .exhibitionList(exhibitionList)
        .detailLocation(exhibition.getEventSchedules().get(0).getDetailLocation())
        .price(exhibition.getPrice())
        .location(locationDTO)
        .createdTime(exhibition.getCreatedTime())
        .updatedTime(exhibition.getUpdatedTime())
        .build();
  }
}
