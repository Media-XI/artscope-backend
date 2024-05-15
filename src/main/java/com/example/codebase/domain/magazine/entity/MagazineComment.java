package com.example.codebase.domain.magazine.entity;

import com.example.codebase.domain.magazine.dto.MagazineCommentRequest;
import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "magazine_comment")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted = false")
public class MagazineComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_comment_id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "mention_username")
    private String mentionUsername;

    @Builder.Default
    @Column(name = "likes")
    private Integer likes = 0;

    @Builder.Default
    @Column(name = "comments")
    private Integer comments = 0;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Builder.Default
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private MagazineComment parentComment;

    @Builder.Default
    @BatchSize(size = 10) // TODO : 배치 사이즈를 부모 댓글 개수에 따라 조절
    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
    private List<MagazineComment> childComments = new ArrayList<>();

    public static MagazineComment toEntity(MagazineCommentRequest.Create newComment, Member member, Magazine magazine) {
        MagazineComment entity = MagazineComment.builder()
                .comment(newComment.getComment())
                .member(member)
                .build();
        // 매거진 양방향 매핑
        entity.setMagazine(magazine);
        return entity;
    }

    private void setMagazine(Magazine magazine) {
        if (this.magazine != null) {
            this.magazine.getMagazineComments().remove(this);
        }
        this.magazine = magazine;
        magazine.addComment(this);
    }

    public void mentionComment(MagazineComment mentionTarget) {
        this.mentionUsername = mentionTarget.getUsername();
        setParentComment(mentionTarget.getParentComment());
    }

    private String getUsername() {
        return this.getMember().getUsername();
    }

    public void setParentComment(MagazineComment parentComment) {
        if (hasParentComment()) {
            this.parentComment.childComments.remove(this);
        }
        this.parentComment = parentComment;
        parentComment.addChildComment(this);
    }

    public boolean hasParentComment() {
        return this.parentComment != null;
    }

    private void addChildComment(MagazineComment childComment) {
        this.childComments.add(childComment);
        this.comments = this.childComments.size(); // 댓글 수 업데이트
    }

    public Long getParentCommentId() {
        return this.parentComment == null ? null : this.parentComment.getId();
    }

    public Boolean isCommentAuthor(Member member) {
        return this.member.equals(member);
    }

    public void update(MagazineCommentRequest.Update updateComment) {
        this.comment = updateComment.getComment();
        this.updatedTime = LocalDateTime.now();
    }


    /**
     * 댓글 삭제
     * SOFT DELETE
     * 매거진 양방향 매핑 해제
     */
    public void delete() {
        this.isDeleted = true;
        this.updatedTime = LocalDateTime.now();
        // 매거진 양방향 매핑 해제
        this.magazine.getMagazineComments().remove(this);
        // 부모댓글이 존재할 경우 양방향 매핑 해제
        if (hasParentComment()) {
            this.parentComment.getChildComments().remove(this);
        }
    }
}
