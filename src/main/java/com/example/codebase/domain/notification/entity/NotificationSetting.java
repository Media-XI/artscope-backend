package com.example.codebase.domain.notification.entity;

import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_setting")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(NotificationSettingIds.class)
public class NotificationSetting {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)")
    private Member member;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "setting_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "is_receive", nullable = false)
    private Boolean isReceive;

    public void updateReceive(){
        this.isReceive = !this.isReceive;
    }
}
