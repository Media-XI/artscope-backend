package com.example.codebase.domain.event.entity;


import com.example.codebase.domain.event.crawling.dto.eventDetailResponse.XmlEventDetailData;
import com.example.codebase.domain.event.dto.EventCreateDTO;
import com.example.codebase.domain.event.dto.EventUpdateDTO;
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
import java.util.List;
import java.util.Objects;

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

    public static Event of(XmlEventDetailData eventData, Member admin){
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
            this.location.removeEvent(this);
            this.location = location;
            location.addEvent(this);
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

    public void updateEventIfChanged(XmlEventDetailData detailEventData, Location location) {
        if(!this.title.equals(detailEventData.getTitle())){
            this.title = detailEventData.getTitle();
        }
        if(!Objects.equals(this.description, detailEventData.getContents1() + "\n" + detailEventData.getContents2())) {
            this.description = detailEventData.getContents1() + "\n" + detailEventData.getContents2();
        }
        if(!Objects.equals(this.detailLocation, detailEventData.getPlace())){
            this.detailLocation = detailEventData.getPlace();
        }
        if(!Objects.equals(this.price, detailEventData.getPrice())){
            this.price = detailEventData.getPrice();
        }
        if(!Objects.equals(this.link, detailEventData.getUrl())){
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
        this.location.addEvent(this);
    }

    public void delete() {
        this.location.removeEvent(this);
        location = null;
        this.enabled = false;
    }
}
