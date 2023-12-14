package com.example.codebase.domain.exhibition.entity;


import com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse.XmlDetailExhibitionData;
import com.example.codebase.domain.exhibition.dto.EventCreateDTO;
import com.example.codebase.domain.exhibition.dto.EventUpdateDTO;
import com.example.codebase.domain.exhibition.dto.ExhbitionCreateDTO;
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
import java.time.format.DateTimeFormatter;
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

    @Builder.Default
    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "seq")
    private Long seq;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

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

        return Event.builder()
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .detailLocation(exhibition.getEventSchedules().get(0).getDetailLocation())
                .price(exhibition.getPrice())
                .link(exhibition.getLink())
                .type(exhibition.getType())
                .enabled(exhibition.getEnabled())
                .seq(exhibition.getSeq())
                .startDate(exhibition.getEventSchedules().get(0).getStartDateTime().toLocalDate())
                .endDate(exhibition.getEventSchedules().get(0).getEndDateTime().toLocalDate())
                .detailedSchedule(exhibition.getEventSchedules().get(0).getDetailLocation())

                .startDate(earliestSchedule.getStartDateTime().toLocalDate())
                .endDate(latestSchedule.getStartDateTime().toLocalDate())

                .createdTime(exhibition.getCreatedTime())
                .updatedTime(exhibition.getUpdatedTime() != null ? exhibition.getUpdatedTime() : exhibition.getCreatedTime())

                .member(exhibition.getMember())
                .location(location)
                .build();
    }

    public void setEventMedias(List<EventMedia> eventMedias) {
        this.eventMedias = eventMedias;
    }

    public static Event of(EventCreateDTO dto, Member member, Location location){
        return Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .detailLocation(dto.getDetailLocation())
                .price(dto.getPrice())
                .link(dto.getLink())
                .type(dto.getEventType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .detailedSchedule(dto.getDetailedSchedule())
                .member(member)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .location(location)
                .build();
    }

    public static Event of(XmlDetailExhibitionData eventData, Member admin){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Event.builder()
                .title(eventData.getTitle())
                .description(eventData.getContents1() + "\n" + eventData.getContents2())
                .detailLocation(eventData.getPlace())
                .price(eventData.getPrice())
                .link(eventData.getUrl())
                .startDate(LocalDate.parse(eventData.getStartDate(), formatter))
                .endDate(LocalDate.parse(eventData.getEndDate(), formatter))
                .member(admin)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .seq(eventData.getSeq())
                .build();
    }

    public void addEventMedia(EventMedia eventMedia){
        this.eventMedias.add(eventMedia);
    }

    public void update(EventUpdateDTO dto) {
        update(dto, null);
    }

    public void update(Location location) {
        update(null, location);
    }

    private void update(EventUpdateDTO dto, Location location) {
        if(dto.getTitle() != null){
            this.title = dto.getTitle();
        }
        if (dto.getDescription() != null){
            this.description = dto.getDescription();
        }
        if (dto.getDetailLocation() != null){
            this.detailLocation = dto.getDetailLocation();
        }
        if (dto.getPrice() != null){
            this.price = dto.getPrice();
        }
        if (dto.getLink() != null){
            this.link = dto.getLink();
        }
        if (dto.getEventType() != null){
            this.type = dto.getEventType();
        }
        if (dto.getStartDate() != null){
            this.startDate = dto.getStartDate();
        }
        if (dto.getEndDate() != null){
            this.endDate = dto.getEndDate();
        }
        if (dto.getDetailedSchedule() != null){
            this.detailedSchedule = dto.getDetailedSchedule();
        }
        if (dto.getLocationId() != null){
            this.location = location;
        }
        this.updatedTime = LocalDateTime.now();
    }

    public boolean equalUsername(String username) {
        return this.member.getUsername().equals(username);
    }

    public void setType(EventType eventType) {
        this.type = eventType;
    }

    public boolean isPersist() {
        return this.id != null;
    }

    public void updateEventIfChanged(XmlDetailExhibitionData detailEventData, Location location) {
        if(!this.title.equals(detailEventData.getTitle())){
            this.title = detailEventData.getTitle();
        }
        if(!this.description.equals(detailEventData.getContents1() + "\n" + detailEventData.getContents2())) {
            this.description = detailEventData.getContents1() + "\n" + detailEventData.getContents2();
        }
        if(!this.detailLocation.equals(detailEventData.getPlace())){
            this.detailLocation = detailEventData.getPlace();
        }
        if(!this.price.equals(detailEventData.getPrice())){
            this.price = detailEventData.getPrice();
        }
        if(!this.link.equals(detailEventData.getUrl())){
            this.link = detailEventData.getUrl();
        }
        if(!this.startDate.equals(LocalDate.parse(detailEventData.getStartDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))){
            this.startDate = LocalDate.parse(detailEventData.getStartDate());
        }
        if(!this.endDate.equals(LocalDate.parse(detailEventData.getEndDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))){
            this.endDate = LocalDate.parse(detailEventData.getEndDate());
        }
        if(!this.location.equals(location)){
            this.location = location;
        }
        this.updatedTime = LocalDateTime.now();

    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
