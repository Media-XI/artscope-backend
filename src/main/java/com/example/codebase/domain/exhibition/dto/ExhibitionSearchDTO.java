package com.example.codebase.domain.exhibition.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionSearchDTO {

    @Builder.Default
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.of(1900, 1, 1);

    @Builder.Default
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.of(2100, 12, 31);

    @NotNull
    private String eventType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime startDateTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime endDateTime;

    public void repeatTimeValidity() {
        if (this.startDateTime.isAfter(this.endDateTime)) {
            throw new RuntimeException("시작일은 종료일보다 이전에 있어야 합니다.");
        }
    }

    public void changeTimeFormat() {
        this.startDateTime = this.startDate.atStartOfDay();
        this.endDateTime = LocalDateTime.of(this.endDate, LocalTime.MAX);
    }
}
