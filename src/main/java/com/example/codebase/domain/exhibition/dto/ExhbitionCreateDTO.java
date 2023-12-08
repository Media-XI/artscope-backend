package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventType;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class ExhbitionCreateDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "설명은 필수입니다.")
    private String description;

    private String price;

    @NotEmpty(message = "링크는 필수입니다.")
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요. ")
    private String link;

    @NotNull(message = "이벤트 타입은 필수입니다.")
    private EventType eventType;

    // 스케쥴 생성 DTO
    @Valid
    private List<EventScheduleCreateDTO> schedule;

    @Valid
    private List<ExhibitionMediaCreateDTO> medias;

    @Valid
    private ExhibitionMediaCreateDTO thumbnail;
}
