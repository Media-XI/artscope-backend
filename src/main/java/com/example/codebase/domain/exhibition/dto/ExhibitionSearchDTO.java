package com.example.codebase.domain.exhibition.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionSearchDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.of(1900, 1, 1);

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.of(2100, 12, 31);

    @NotNull
    private String eventType;

    public void repeatTimeValidity() {
        if (this.startDate.isAfter(this.endDate)) {
            throw new RuntimeException("시작일은 종료일보다 이전에 있어야 합니다.");
        }
    }
}
