package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class ExhbitionCreateDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "설명은 필수입니다.")
    private String description;

    @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
    private int price;

    @NotEmpty(message = "링크는 필수입니다.")
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요. ")
    private String link;

    @NotNull(message = "이벤트 타입은 필수입니다.")
    private EventType eventType;

    // 스케쥴 생성 DTO
    private List<EventScheduleCreateDTO> schedule;

    private List<ExhibitionMediaCreateDTO> medias;

    private ExhibitionMediaCreateDTO thumbnail;
}
