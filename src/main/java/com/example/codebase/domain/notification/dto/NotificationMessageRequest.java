package com.example.codebase.domain.notification.dto;

import com.example.codebase.domain.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificationMessageRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String message;

    @NotNull(message = "알림의 타입을 기입해주세요 ")
    private NotificationType notificationType;

    public void validAdminNotificationType() {
        this.notificationType.validAdminNotificationType();
    }
}
