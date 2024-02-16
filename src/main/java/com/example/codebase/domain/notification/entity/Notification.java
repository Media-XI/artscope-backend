package com.example.codebase.domain.notification.entity;

import com.example.codebase.controller.NotificationController;
import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notification")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "message", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Builder.Default
    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationReceivedStatus> notificationReceivedStatus = new ArrayList<>();

    public static Notification of(Member member, String jsonMessage, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .message(jsonMessage)
                .type(notificationType)
                .createdTime(LocalDateTime.now())
                .build();

        notification.addNotificationRecipient(NotificationReceivedStatus.of(member, notification));
        return notification;
    }

    public static Notification of(List<Member> members, String jsonMessage, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .message(jsonMessage)
                .type(notificationType)
                .createdTime(LocalDateTime.now())
                .build();

        members.forEach(member -> notification.addNotificationRecipient(NotificationReceivedStatus.of(member, notification)));
        return notification;
    }

    private void addNotificationRecipient(NotificationReceivedStatus notificationReceivedStatus) {
        this.notificationReceivedStatus.add(notificationReceivedStatus);
    }
}
