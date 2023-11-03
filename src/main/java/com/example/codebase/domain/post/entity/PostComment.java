package com.example.codebase.domain.post.entity;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.post.dto.PostCommentCreateDTO;
import com.example.codebase.domain.post.dto.PostCommentUpdateDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static PostComment of(PostCommentCreateDTO commentCreateDTO, Member author) {
        return PostComment.builder()
                .content(commentCreateDTO.getContent())
                .author(author)
                .createdTime(LocalDateTime.now())
                .build();
    }

    public void setMentionUsername(String mentionUsername) {
        this.mentionUsername = mentionUsername;
    }

    public void setParent(PostComment parent) {
        if (this.parent != null) {
            this.parent.childComments.remove(this);
        }
        this.parent = parent;
        this.parent.childComments.add(this);
        this.parent.comments = this.parent.childComments.size();
    }

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getPostComment().remove(this);
        }
        this.post = post;
        post.addComment(this);
    }

    public void update(PostCommentUpdateDTO commentUpdateDTO) {
        this.content = commentUpdateDTO.getContent();
        this.updatedTime = LocalDateTime.now();
    }

    public void fireRemove() {
        post.removeComment(this);
        this.post = null;

        if (parent != null) {
            parent.childComments.remove(this);
            parent.comments = parent.childComments.size();
            this.parent = null;
        }

        this.author = null;
        // TODO : 자식 대댓글 까지는 cascade 삭제.
    }
}
