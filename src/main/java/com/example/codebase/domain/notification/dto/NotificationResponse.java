package com.example.codebase.domain.notification.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.notification.entity.Notification;
import com.example.codebase.domain.notification.entity.NotificationType;
import com.example.codebase.domain.notification.entity.NotificationWithIsRead;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {

    @Getter
    @Setter
    @Schema(name = "NotificationResponse.Get", description = "알림 개별 응답")
    public static class Get {
        private Long notificationId;

        @JsonFormat
        private String message;

        private String type;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private String createdTime;

        private Boolean isRead;

        public static Get from(NotificationWithIsRead notification) {
            Get response = new Get();
            response.notificationId = notification.getNotification().getNotificationId();
            response.message = notification.getNotification().getMessage();
            response.type = notification.getNotification().getType().name();
            response.createdTime = notification.getNotification().getCreatedTime().toString();
            response.isRead = notification.getIsRead();
            return response;
        }

        public static Get from(Notification notification){
            Get response = new Get();
            response.notificationId = notification.getNotificationId();
            response.message = notification.getMessage();
            response.type = notification.getType().name();
            response.createdTime = notification.getCreatedTime().toString();
            response.isRead = true;
            return response;
        }
    }

    @Getter
    @Setter
    @Schema(name = "NotificationResponse.GetAll", description = "알림 전체 응답")
    public static class GetAll {
        private List<Get> notifications;

        private PageInfo pageInfo;

        public static GetAll from(Page<NotificationWithIsRead> notifications) {
            GetAll response = new GetAll();
            response.pageInfo = PageInfo.from(notifications);
            response.notifications = notifications.stream()
                    .map(NotificationResponse.Get::from)
                    .toList();
            return response;
        }

    }

    @Getter
    @Setter
    @Schema(description = "알림 이벤트 응답")
    public static class EventMessage {

        private String message;

        private int count;

        private NotificationType type;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private String sendTime;

        public static EventMessage from(String message, int count,NotificationType type, String sendTime) {
            EventMessage response = new EventMessage();
            response.message = message;
            response.sendTime = sendTime;
            response.type = type;
            response.count = count;
            return response;
        }

        public void update(Notification notification) {
            this.message = notification.getMessage();
            this.count++;
            this.type = notification.getType();
            this.sendTime = LocalDateTime.now().toString();
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
