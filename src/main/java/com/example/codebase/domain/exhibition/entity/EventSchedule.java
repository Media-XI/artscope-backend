package com.example.codebase.domain.exhibition.entity;

import com.example.codebase.domain.exhibition.dto.EventScheduleCreateDTO;
import com.example.codebase.domain.location.entity.Location;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_schedule")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_schedule_id")
  private Long id;

  @Column(name = "event_date")
  private LocalDateTime eventDate;

  @Column(name = "start_time")
  private LocalDateTime startTime;

  @Column(name = "end_time")
  private LocalDateTime endTime;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @Column(name = "detail_location")
  private String detailLocation;

  @ManyToOne
  @JoinColumn(name = "event_id", nullable = false)
  private Exhibition exhibition;

  @Builder.Default
  @OneToMany(mappedBy = "eventSchedule", cascade = CascadeType.ALL)
  private List<ExhibitionParticipant> exhibitionParticipants = new ArrayList<>();

  @Column(name = "created_time")
  private LocalDateTime createdTime;

  @Column(name = "updated_time")
  private LocalDateTime updatedTime;

  public static EventSchedule from(EventScheduleCreateDTO scheduleDTO) {
    return EventSchedule.builder()
        .eventDate(scheduleDTO.getEventDate())
        .startTime(scheduleDTO.getStartTime())
        .endTime(scheduleDTO.getEndTime())
        .detailLocation(scheduleDTO.getDetailLocation())
        .createdTime(LocalDateTime.now())
        .build();
  }

  // Event 양방향 연관 메소드
  public void setEvent(Exhibition exhibition) {
    this.exhibition = exhibition;
    exhibition.addEventSchedule(this);
  }

  public Location getLocation() {
    return this.location;
  }

  // Location 단방향 연관 메소드
  public void setLocation(Location location) {
    this.location = location;
  }

  public void getDetailLocation(String detailLocation) {
    this.detailLocation = detailLocation;
  }

  public void delete() {
    this.exhibition = null;
  }
}
