package com.example.codebase.domain.artwork.entity;

import com.example.codebase.domain.artwork.dto.ArtworkCommentCreateDTO;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artwork_comment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ArtworkComment {

    @Id
    @Column(name = "artwork_comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ArtworkComment parentComment;

    @Builder.Default
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<ArtworkComment> childComments = new ArrayList<>();

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    public static ArtworkComment of(ArtworkCommentCreateDTO commentCreateDTO, Artwork artwork, Member member) {
        return ArtworkComment.builder()
                .content(commentCreateDTO.getContent())
                .artwork(artwork)
                .author(member)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public void addChildComment(ArtworkComment childComment) {
        this.childComments.add(childComment);
        childComment.parentComment = this;
    }
}
