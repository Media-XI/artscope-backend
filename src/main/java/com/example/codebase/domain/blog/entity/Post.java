package com.example.codebase.domain.blog.entity;

import com.example.codebase.domain.blog.dto.PostCreateDTO;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name="content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    public static Post of(PostCreateDTO postCreateDTO, Member author) {
        return Post.builder()
                .title(postCreateDTO.getTitle())
                .content(postCreateDTO.getContent())
                .author(author)
                .createdTime(LocalDateTime.now())
                .build();
    }
}
