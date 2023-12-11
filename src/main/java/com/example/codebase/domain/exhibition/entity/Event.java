package com.example.codebase.domain.exhibition.entity;


import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "event")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "enabled = true")
public class Event {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "detail_location", columnDefinition = "varchar(255)")
    private String detailLocation;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "link", length = 200)
    private String link;

    @Builder.Default
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType type = EventType.STANDARD;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "seq")
    private Long seq;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; //TODO : LocalDate으로 변경

    @Column(name = "end_date" , nullable = false)
    private LocalDate endDate;

    @Column(name =" detailed_schedule")
    private String detailedSchedule;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)", nullable = false)
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventMedia> eventMedias = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    public static Event from(Exhibition exhibition) {
        System.out.println("exhibition = " + exhibition.getId());

        if(exhibition.getEventSchedules().isEmpty()){
            return null;
        }

        EventSchedule earliestSchedule = exhibition.getEventSchedules().stream()
                .min(Comparator.comparing(EventSchedule::getStartDateTime))
                .orElse(null);

        EventSchedule latestSchedule = exhibition.getEventSchedules().stream()
                .max(Comparator.comparing(EventSchedule::getStartDateTime))
                .orElse(null);

        Location location = exhibition.getEventSchedules().get(0).getLocation();

        Event event =  Event.builder()
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .detailLocation(exhibition.getEventSchedules().get(0).getDetailLocation())
                .price(exhibition.getPrice())
                .link(exhibition.getLink())
                .type(exhibition.getType())
                .enabled(exhibition.getEnabled())
                .seq(exhibition.getSeq())
                .startDate(exhibition.getEventSchedules().get(0).getStartDateTime())
                .endDate(exhibition.getEventSchedules().get(0).getEndDateTime().toLocalDate())
                .detailedSchedule(exhibition.getEventSchedules().get(0).getDetailLocation())

                .startDate(earliestSchedule.getStartDateTime())
                .endDate(latestSchedule.getStartDateTime().toLocalDate())


                .createdTime(exhibition.getCreatedTime())
                .updatedTime(exhibition.getUpdatedTime() != null ? exhibition.getUpdatedTime() : exhibition.getCreatedTime())

                .member(exhibition.getMember())
                .location(location)
                .build();

        location.addEvent(event);

        return event;
    }

    public void setEventMedias(List<EventMedia> eventMedias) {
        this.eventMedias = eventMedias;
    }
}
