package com.example.codebase.domain.curation.entity;

import com.example.codebase.domain.curation.dto.CurationRequest;
import com.example.codebase.domain.magazine.entity.Magazine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "curation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Curation {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_id", unique = true)
    private Magazine magazine;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;


    public void clearMagazine() {
        this.magazine = null;
        this.updatedTime = LocalDateTime.now();
    }

    public void updateCuration(CurationRequest curationRequest, Curation curation) {
    }

    public void setMagazine(Magazine megazine) {
        this.magazine = megazine;
        this.updatedTime = LocalDateTime.now();
    }
}
