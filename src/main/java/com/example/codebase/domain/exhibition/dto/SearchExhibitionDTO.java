package com.example.codebase.domain.exhibition.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@NoArgsConstructor
@AllArgsConstructor
public class SearchExhibitionDTO {

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  public LocalDateTime getStartDate() {
    return startDate.atStartOfDay();
  }

  public LocalDateTime getEndDate() {
    return endDate.atTime(23, 59, 59);
  }
}
