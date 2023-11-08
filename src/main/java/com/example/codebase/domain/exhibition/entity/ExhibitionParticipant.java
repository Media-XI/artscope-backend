package com.example.codebase.domain.exhibition.entity;

import com.example.codebase.domain.member.entity.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_participant")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionParticipant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "exhibition_participant_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "event_schedule_id", nullable = false)
  private EventSchedule eventSchedule;

  @Column(name = "name")
  private String name;

  public static ExhibitionParticipant of(Member member, EventSchedule eventSchedule) {
    return ExhibitionParticipant.builder().member(member).eventSchedule(eventSchedule).build();
  }

  public void setMember(Member member) {
    this.member = member;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEventSchedule(EventSchedule eventSchedule) {
    this.eventSchedule = eventSchedule;
  }
}
