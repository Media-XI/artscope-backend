package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventType;
import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExhibitionUpdateDTO {

    @Parameter(required = false)
    private String title;

    @Parameter(required = false)
    private String description;

    @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @Parameter(required = false)
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요. ")
    private String link;

    @Parameter(required = false)
    private EventType eventType;
}
