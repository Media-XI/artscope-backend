package com.example.codebase.domain.notification.dto;

import com.example.codebase.domain.notification.entity.NotificationSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public class NotificationSettingResponse {

    @Getter
    @Setter
    @Schema(name = "NotificationSettingResponse.GetALL", description = "알림 설정 조회 응답")
    public static class GetAll{
        private Boolean receiveMention;
        private Boolean receiveUpdate;
        private Boolean receiveAnnouncement;
        private Boolean receiveNewFollower;
        private Boolean receivePromotionalNews;

        public static GetAll from(NotificationSetting notificationSetting){
            GetAll response = new GetAll();
            response.receiveMention = notificationSetting.getReceiveMention();
            response.receiveUpdate = notificationSetting.getReceiveUpdate();
            response.receiveAnnouncement = notificationSetting.getReceiveAnnouncement();
            response.receiveNewFollower = notificationSetting.getReceiveNewFollower();
            response.receivePromotionalNews = notificationSetting.getReceivePromotionalNews();
            return response;
        }
    }

}
