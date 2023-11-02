package com.example.codebase.domain.exhibition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchExhibitionDTO {

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  @NotNull private String eventType;

  public LocalDateTime getStartDate() {
    return startDate.atStartOfDay();
  }

  public LocalDateTime getEndDate() {
    return endDate.atTime(23, 59, 59);
  }

  public void repeatTimeValidity() {
    if (this.getStartDate().isAfter(this.getEndDate())) {
      throw new RuntimeException("시작일은 종료일보다 이전에 있어야 합니다.");
    }
  }
}
