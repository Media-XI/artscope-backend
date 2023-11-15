package com.example.codebase.domain.post.entity;

import com.example.codebase.domain.media.MediaType;
import com.example.codebase.domain.post.dto.PostMediaCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_media")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostMedia {

    @Id
    @Column(name = "post_media_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Getter
    private String mediaUrl;

    private Integer mediaWidth;

    private Integer mediaHeight;

    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public static PostMedia from(PostMediaCreateDTO thumbnail) {
        return PostMedia.builder()
            .mediaType(MediaType.create(thumbnail.getMediaType()))
            .mediaUrl(thumbnail.getMediaUrl())
            .mediaWidth(thumbnail.getWidth())
            .mediaHeight(thumbnail.getHeight())
            .createdTime(LocalDateTime.now())
            .build();
    }


    // 연관관계 메소드
    public void setPost(Post post) {
        if (this.post != null) {
            this.post.removeMedia(this);
        }

        this.post = post;
        post.addMedia(this);
    }
}
