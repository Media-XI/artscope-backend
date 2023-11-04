package com.example.codebase.domain.agora.entity;

import com.example.codebase.domain.agora.dto.AgoraMediaCreateDTO;
import com.example.codebase.domain.media.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    public static AgoraMedia of(AgoraMediaCreateDTO thumbnail, Agora agora) {
        AgoraMedia agoraMedia = AgoraMedia.builder()
                .mediaType(MediaType.create(thumbnail.getMediaType()))
                .mediaUrl(thumbnail.getMediaUrl())
                .mediaWidth(thumbnail.getWidth())
                .mediaHeight(thumbnail.getHeight())
                .createdTime(LocalDateTime.now())
                .build();
        agoraMedia.setAgora(agora);
        return agoraMedia;
    }

    private void setAgora(Agora agora) {
        this.agora = agora;
        agora.addMedia(this);
    }

    public void delete() {
        this.agora = null;
    }
}
