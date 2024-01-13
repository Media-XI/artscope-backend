package com.example.codebase.domain.location.entity;

import com.example.codebase.domain.event.crawling.dto.eventDetailResponse.XmlEventDetailData;
import com.example.codebase.domain.event.entity.Event;
import com.example.codebase.domain.location.dto.LocationCreateDTO;
import com.example.codebase.domain.location.dto.LocationUpdateDTO;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "english_name", length = 255)
    private String englishName;

    @Column(name = "phone_number", length = 150)
    private String phoneNumber;

    @Column(name = "web_site_url", length = 255)
    private String webSiteUrl;

    @Column(name = "sns_url", length = 255)
    private String snsUrl;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "location")
    private List<Event> events = new ArrayList<>();

    public static Location of(LocationCreateDTO dto, Member member) {
        return Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress())
                .name(dto.getName())
                .englishName(dto.getEnglishName())
                .phoneNumber(dto.getPhoneNumber())
                .webSiteUrl(dto.getWebSiteUrl())
                .snsUrl(dto.getSnsUrl())
                .member(member)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public static Location of(XmlEventDetailData perforInfo, Member member) {
        return Location.builder()
                .latitude(perforInfo.getLatitude())
                .longitude(perforInfo.getLongitude())
                .address(perforInfo.getPlaceAddr())
                .name(perforInfo.getPlace())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .member(member)
                .build();
    }

    public boolean hasEvents() {
        return !this.events.isEmpty();
    }

    public void update(LocationUpdateDTO dto) {
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.address = dto.getAddress();
        this.name = dto.getName();
        this.englishName = dto.getEnglishName();
        this.phoneNumber = dto.getPhoneNumber();
        this.webSiteUrl = dto.getWebSiteUrl();
        this.snsUrl = dto.getSnsUrl();
    }

    public boolean equalsUsername(String username) {
        if (this.member == null) {
            return false;
        }
        return this.member.getUsername().equals(username);
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void removeEvent(Event event) {
        this.events.remove(event);
    }
}
