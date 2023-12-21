package com.example.codebase.domain.Event.entity;

import com.example.codebase.domain.Event.crawling.dto.eventDetailResponse.XmlEventDetailData;
import com.example.codebase.domain.Event.dto.EventMediaCreateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event_media")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventMedia {

    @Id
    @Column(name = "event_media_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private EventMediaType eventMediaType;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public static EventMedia of(EventMediaCreateDTO mediaCreateDTO, Event event) {
        return EventMedia.builder()
                .eventMediaType(EventMediaType.create(mediaCreateDTO.getMediaType()))
                .mediaUrl(mediaCreateDTO.getMediaUrl())
                .event(event)
                .createdTime(LocalDateTime.now())
                .build();
    }

    public static EventMedia from(XmlEventDetailData detailExhibitionData, Event event) {
        return EventMedia.builder()
                .eventMediaType(EventMediaType.image)
                .mediaUrl(detailExhibitionData.getImgUrl())
                .event(event)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public void update(EventMediaCreateDTO thumbnail) {
        this.mediaUrl = thumbnail.getMediaUrl();
        this.eventMediaType = EventMediaType.create(thumbnail.getMediaType());
        this.updatedTime = LocalDateTime.now();
    }
}
