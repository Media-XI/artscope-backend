package com.example.codebase.domain.curation.entity;

import com.example.codebase.domain.magazine.entity.Magazine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "curation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "is_deleted = false")
public class Curation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    @Builder.Default
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void delete(){
        this.isDeleted = true;
        this.updatedTime = LocalDateTime.now();
    }

    public void setMagazine(Magazine megazine) {
        this.magazine = megazine;
        this.updatedTime = LocalDateTime.now();
    }

    public void setUpdatedTime() {
        this.updatedTime = LocalDateTime.now();
    }
}
