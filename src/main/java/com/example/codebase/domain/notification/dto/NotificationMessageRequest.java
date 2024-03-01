package com.example.codebase.domain.notification.dto;

import com.example.codebase.domain.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String message;

    @NotNull(message = "알림 타입을 입력해주세요.")
    private NotificationType notificationType;

    public void validAdminNotificationType() {
        this.notificationType.validAdminNotificationType();
    }
}
