package com.example.codebase.domain.exhibition.entity;

import com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse.PerforInfo;
import com.example.codebase.domain.exhibition.dto.ExhbitionCreateDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionUpdateDTO;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exhibition")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "enabled = true")
public class Exhibition {

    @Id
    @Column(name = "exhibition_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "link", length = 500)
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
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)", nullable = false)
    private Member member;

    @Builder.Default
    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "seq")
    private Long seq;

    public static Exhibition of(ExhbitionCreateDTO dto, Member member) {
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

    public static Exhibition of(PerforInfo perforInfo, Member member) {
        return Exhibition.builder()
                .title(perforInfo.getTitle())
                .description(perforInfo.getContents1() + "\n" + perforInfo.getContents2())
                .price(perforInfo.getPrice())
                .link(perforInfo.getUrl())
                .createdTime(LocalDateTime.now())
                .member(member)
                .seq(perforInfo.getSeq())
                .build();
    }

    public void update(ExhibitionUpdateDTO exhibitionUpdateDTO) {
        this.title =
                exhibitionUpdateDTO.getTitle() != null ? exhibitionUpdateDTO.getTitle() : this.title;
        this.description =
                exhibitionUpdateDTO.getDescription() != null
                        ? exhibitionUpdateDTO.getDescription()
                        : this.description;
        this.link = exhibitionUpdateDTO.getLink() != null ? exhibitionUpdateDTO.getLink() : this.link;
        this.type =
                exhibitionUpdateDTO.getEventType() != null ? exhibitionUpdateDTO.getEventType() : this.type;
        this.price =
                exhibitionUpdateDTO.getPrice() != null ? exhibitionUpdateDTO.getPrice() : this.price;
        this.updatedTime = LocalDateTime.now();
    }

    public void delete() {
        this.enabled = false;
    }

    public void addExhibitionMedia(ExhibitionMedia media) {
        this.exhibitionMedias.add(media);
    }

    public void addEventSchedule(EventSchedule eventSchedule) {
        this.eventSchedules.add(eventSchedule);
    }

    public EventSchedule getFirstEventSchedule() {
        return this.eventSchedules.get(0);
    }

    public Boolean equalUsername(String username) {
        return this.member.getUsername().equals(username);
    }

    public void removeEventSchedule(EventSchedule eventSchedule) {
        this.eventSchedules.remove(eventSchedule);
    }

    public void setEventSchedules(List<EventSchedule> eventSchedules) {
        this.eventSchedules = eventSchedules;
    }

    public void setType(EventType eventType) {
        this.type = eventType;
    }

    public Exhibition update(PerforInfo perforInfo, Member member) {
        this.title = perforInfo.getTitle();
        this.description = perforInfo.getContents1() + "\n" + perforInfo.getContents2();
        this.price = perforInfo.getPrice();
        this.link = perforInfo.getUrl();
        this.updatedTime = LocalDateTime.now();
        this.member = member;
        this.enabled = true;
        return this;
    }

    public boolean hasChanged(PerforInfo perforInfo) {
        if (hasEventScheduleChanged(perforInfo)) {
            return true;
        }
        return hasChangedExhibition(perforInfo);
    }

    private boolean hasEventScheduleChanged(PerforInfo perforInfo) {
        LocalDate startDate = LocalDate.parse(perforInfo.getStartDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate endDate = LocalDate.parse(perforInfo.getEndDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));

        return !this.eventSchedules.get(0).getStartDateTime().equals(startDate.atStartOfDay())
                || !this.eventSchedules.get(this.eventSchedules.size() - 1).getEndDateTime().equals(endDate.plusDays(1).atStartOfDay());
    }


        private boolean hasChangedExhibition(PerforInfo perforInfo){
            return !this.title.equals(perforInfo.getTitle())
                    || !this.description.equals(perforInfo.getContents1() + "\n" + perforInfo.getContents2())
                    || !this.price.equals(perforInfo.getPrice())
                    || !this.link.equals(perforInfo.getUrl());
        }
    }

