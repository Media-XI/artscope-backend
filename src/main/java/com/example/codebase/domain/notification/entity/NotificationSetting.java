package com.example.codebase.domain.notification.entity;

import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "notification_setting")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSetting {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)")
    private Member member;

    @Builder.Default
    @Column(name = "receive_mention", nullable = false)
    private Boolean receiveMention = true;

    @Builder.Default
    @Column(name = "receive_update", nullable = false)
    private Boolean receiveUpdate = true;

    @Builder.Default
    @Column(name = "receive_announcement", nullable = false)
    private Boolean receiveAnnouncement = true;

    @Builder.Default
    @Column(name = "receive_new_follower", nullable = false)
    private Boolean receiveNewFollower = true;

    @Builder.Default
    @Column(name = "receive_promotional_news", nullable = false)
    private Boolean receivePromotionalNews = true;

    public static NotificationSetting from(Member member) {
        return NotificationSetting.builder()
                .id(member.getId())
                .member(member)
                .build();
    }

    public void updateReceive(NotificationType type) {
        switch (type) {
            case MENTION:
                this.receiveMention = !this.receiveMention;
                break;
            case UPDATE:
                this.receiveUpdate = !this.receiveUpdate;
                break;
            case ANNOUNCEMENT:
                this.receiveAnnouncement = !this.receiveAnnouncement;
                break;
            case NEW_FOLLOWER:
                this.receiveNewFollower = !this.receiveNewFollower;
                break;
            case PROMOTIONAL_NEWS:
                this.receivePromotionalNews = !this.receivePromotionalNews;
                break;
        }
    }
}
