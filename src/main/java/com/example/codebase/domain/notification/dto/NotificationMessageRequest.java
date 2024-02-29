package com.example.codebase.domain.notification.dto;

import com.example.codebase.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageRequest {

    private String title;

    private String message;

    private NotificationType notificationType;

    public void validAdminNotificationType() {
        this.notificationType.validAdminNotificationType();
    }
}
