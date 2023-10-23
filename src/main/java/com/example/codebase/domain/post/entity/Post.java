package com.example.codebase.domain.post.entity;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.post.dto.PostCreateDTO;
import com.example.codebase.domain.post.dto.PostUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    @Column(name = "views", columnDefinition = "integer default 0", nullable = false)
    private Integer views = 0;

    @Builder.Default
    @Column(name = "likes", nullable = false)
    private Integer likes = 0;

    @Builder.Default
    @Column(name = "comments", nullable = false)
    private Integer comments = 0;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLikeMember> postLikeMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> postComment = new ArrayList<>();

    public static Post of(PostCreateDTO postCreateDTO, Member author) {
        return Post.builder()
                .content(postCreateDTO.getContent())
                .author(author)
                .createdTime(LocalDateTime.now())
                .build();
    }

    public void update(PostUpdateDTO postUpdateDTO) {
        this.content = postUpdateDTO.getContent();
        this.updatedTime = LocalDateTime.now();
    }

    public void incressView() {
        this.views++;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void addLikeMember(PostLikeMember postLikeMember) {
        this.postLikeMembers.add(postLikeMember);
    }

    public void addComment(PostComment postComment) {
        this.postComment.add(postComment);
        this.comments = this.postComment.size();
    }

    public List<PostComment> getPostComment() {
        return this.postComment;
    }

    public void removeComment(PostComment comment) {
        this.postComment.remove(comment);
        this.comments = postComment.size() - comment.getComments();
    }
}
