package com.example.codebase.domain.post.entity;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.post.dto.PostCommentCreateDTO;
import com.example.codebase.domain.post.dto.PostCommentUpdateDTO;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post_comment")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_id")
    private Long id;

    private String content;

    private String mentionUsername;

    @Builder.Default
    @Column(name = "comments")
    private Integer comments = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> childComments = new ArrayList<>();

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public static PostComment of(Post post, PostCommentCreateDTO commentCreateDTO, Member author) {
        return PostComment.builder()
                .content(commentCreateDTO.getContent())
                .post(post)
                .author(author)
                .createdTime(LocalDateTime.now())
                .build();
    }

    public void addComment(PostComment child) {
        this.childComments.add(child);
        this.comments = this.childComments.size();
    }

    public void setMentionUsername(String mentionUsername) {
        this.mentionUsername = mentionUsername;
    }

    public void addParent(PostComment parent) {
        this.parent = parent;
    }

    public void update(PostCommentUpdateDTO commentUpdateDTO) {
        this.content = commentUpdateDTO.getContent();
        this.updatedTime = LocalDateTime.now();
    }
}
