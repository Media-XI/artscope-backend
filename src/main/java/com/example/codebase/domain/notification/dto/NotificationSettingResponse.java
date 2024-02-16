package com.example.codebase.domain.notification.dto;

import com.example.codebase.domain.notification.entity.NotificationSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class NotificationSettingResponse {

    @Getter
    @Setter
    @Schema(name = "NotificationSettingResponse.Get", description = "알림 설정 조회 응답")
    public static class Get{
        private String notificationType;

        private Boolean isReceive;

        public static Get from(NotificationSetting notificationSetting){
            Get response = new Get();
            response.notificationType = notificationSetting.getNotificationType().name();
            response.isReceive = notificationSetting.getIsReceive();
            return response;
        }
    }

    @Getter
    @Setter
    public static class GetAll{
        private List<Get> notificationSettings;

        public static GetAll from(List<NotificationSetting> notificationSettings){
            GetAll response = new GetAll();
            response.notificationSettings = notificationSettings.stream()
                    .map(NotificationSettingResponse.Get::from)
                    .toList();
            return response;
        }
    }
}
