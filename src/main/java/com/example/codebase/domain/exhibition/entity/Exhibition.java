package com.example.codebase.domain.exhibition.entity;

import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition_artwork.entity.ExhibitionArtwork;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Builder.Default
    @Column(name = "enabled")
    private boolean enabled = true;    // 공모전 활성상태 -> 삭제 여부와 같음

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static Exhibition of(CreateExhibitionDTO dto, Member member) {
        return Exhibition.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .createdTime(LocalDateTime.now())
                .member(member)
                .build();
    }

    public void update(CreateExhibitionDTO createExhibitionDTO) {
        this.title = createExhibitionDTO.getTitle();
        this.description = createExhibitionDTO.getDescription();
        this.startDate = createExhibitionDTO.getStartDate();
        this.endDate = createExhibitionDTO.getEndDate();
        this.updatedTime = LocalDateTime.now();
    }

    public void delete() {
        this.enabled = false;
    }
}
