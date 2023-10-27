package com.example.codebase.domain.post.entity;

import com.example.codebase.domain.media.MediaType;
import com.example.codebase.domain.post.dto.PostMediaCreateDTO;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
