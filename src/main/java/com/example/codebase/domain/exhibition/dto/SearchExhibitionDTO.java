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
  private LocalDate endDate;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  public LocalDateTime getStartDate() {
    return startDate.atStartOfDay();
  }

  public LocalDateTime getEndDate() {
    return endDate.plusDays(1).atStartOfDay();
  }

}
