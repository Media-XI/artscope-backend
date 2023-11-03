package com.example.codebase.domain.agoda.entity;

import com.example.codebase.domain.media.MediaType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
@Table(name = "agora_media")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AgoraMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_media_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    private String mediaUrl;

    private Integer mediaWidth;

    private Integer mediaHeight;

    private LocalDateTime createdTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agora_id")
    private Agora agora;

    public void setAgora(Agora agora) {
        this.agora = agora;
        agora.addMedia(this);
    }

    public void delete() {
        this.agora = null;
    }
}