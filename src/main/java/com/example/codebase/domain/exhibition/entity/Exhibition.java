package com.example.codebase.domain.exhibition.entity;

import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.UpdateExhibitionDTO;
import com.example.codebase.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exhibition")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Exhibition {

  @Id
  @Column(name = "exhibition_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "price")
  private int price;

  @Column(name = "link", nullable = false, length = 500)
  private String link;

  @Builder.Default
  @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL)
  private List<ExhibitionMedia> exhibitionMedias = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL)
  private List<EventSchedule> eventSchedules = new ArrayList<>();

  @Column(name = "created_time")
  private LocalDateTime createdTime;

  @Column(name = "updated_time")
  private LocalDateTime updatedTime;

  @Builder.Default
  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private EventType type = EventType.STANDARD;

  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Builder.Default
  @Column(name = "enabled")
  private boolean enabled = true; // 공모전 활성상태 -> 삭제 여부와 같음

  public static Exhibition of(CreateExhibitionDTO dto, Member member) {
    return Exhibition.builder()
        .title(dto.getTitle())
        .description(dto.getDescription())
        .price(dto.getPrice())
        .link(dto.getLink())
        .type(dto.getEventType())
        .member(member)
        .createdTime(LocalDateTime.now())
        .build();
  }

  public void update(UpdateExhibitionDTO updateExhibitionDTO) {
    this.title =
        updateExhibitionDTO.getTitle() != null ? updateExhibitionDTO.getTitle() : this.title;
    this.description =
        updateExhibitionDTO.getDescription() != null
            ? updateExhibitionDTO.getDescription()
            : this.description;
    this.link = updateExhibitionDTO.getLink() != null ? updateExhibitionDTO.getLink() : this.link;
    this.type =
        updateExhibitionDTO.getEventType() != null ? updateExhibitionDTO.getEventType() : this.type;
    this.price = updateExhibitionDTO.getPrice() != null ? updateExhibitionDTO.getPrice() : this.price;
    this.updatedTime = LocalDateTime.now();
  }

  public void delete() {
    this.enabled = false;
    deleteEventSchedules();
  }

  public void deleteEventSchedules() {
    this.eventSchedules.forEach(EventSchedule::delete);
  }

  public void addExhibitionMedia(ExhibitionMedia media) {
    this.exhibitionMedias.add(media);
  }

  public void addEventSchedule(EventSchedule eventSchedule) {
    this.eventSchedules.add(eventSchedule);
  }
}
