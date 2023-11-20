package com.example.codebase.domain.exhibition.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionSearchDTO {

    @Builder.Default
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate = LocalDateTime.of(1900, 1, 1, 0, 0);

    @Builder.Default
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate = LocalDateTime.of(2100, 12, 31, 23, 59);

    @NotNull
    private String eventType;

    public void repeatTimeValidity() {
        if (this.startDate.isAfter(this.endDate)) {
            throw new RuntimeException("시작일은 종료일보다 이전에 있어야 합니다.");
        }
    }
}
