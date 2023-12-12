package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EventCreateDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "설명은 필수입니다.")
    private String description;

    @Parameter(required = false)
    private String price;

    @Parameter(required = false)
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요. ")
    private String link;

    @NotNull(message = "이벤트 타입은 필수입니다.")
    private EventType eventType;

    @NotNull(message = "시작일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Parameter(required = false)
    private String detailedSchedule;

    @NotNull(message = "장소는 필수입니다.")
    private Long locationId;

    @Parameter(required = false)
    private String detailLocation;

    @Valid
    private List<ExhibitionMediaCreateDTO> medias;

    @Valid
    private ExhibitionMediaCreateDTO thumbnail;

    public void validateDates() {
        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("종료일은 시작일보다 빠를 수 없습니다.");
        }
    }

}
