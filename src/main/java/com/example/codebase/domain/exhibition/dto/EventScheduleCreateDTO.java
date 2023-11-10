package com.example.codebase.domain.exhibition.dto;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class EventScheduleCreateDTO {

    @NotBlank(message = "날짜 지정은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime eventDate;

    @NotBlank(message = "시작시간은 필수입니다.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalDateTime startTime;

  @NotBlank(message = "종료시간은 필수입니다.")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalDateTime endTime;

  @NotBlank(message = "장소는 필수입니다.")
  private Long locationId;

  private String detailLocation;

  private List<ParticipantInformationDTO> participants;

  public void checkTimeValidity() {
    if (this.startTime.isEqual(this.endTime)) {
      throw new RuntimeException("시작시간과 종료시간이 같을 수 없습니다.");
    }

    if (this.startTime.isAfter(this.endTime)) {
      throw new RuntimeException("종료시간이 시작시간보다 빠를 수 없습니다.");
    }
  }
}
