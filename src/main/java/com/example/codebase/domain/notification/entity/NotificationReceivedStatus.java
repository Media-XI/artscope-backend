package com.example.codebase.domain.notification.entity;

import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_received_status")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(NotificationReceivedStatusIds.class)
public class NotificationReceivedStatus {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;

    public static NotificationReceivedStatus of(Member member, Notification notification) {
        return NotificationReceivedStatus.builder()
                .member(member)
                .notification(notification)
                .build();
    }

    public void read() {
        this.isRead = true;
    }

    public boolean isRead() {
        return this.isRead;
    }

    public void removeNotification() {
        notification.getNotificationReceivedStatus().remove(this);
    }
}
