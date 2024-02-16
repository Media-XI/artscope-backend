package com.example.codebase.domain.notification.service;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.dto.NotificationSettingResponse;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.notification.entity.NotificationSettingIds;
import com.example.codebase.domain.notification.entity.NotificationType;
import com.example.codebase.domain.notification.repository.NotificationSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;

    @Autowired
    public NotificationSettingService(NotificationSettingRepository notificationSettingRepository) {
        this.notificationSettingRepository = notificationSettingRepository;
    }

    @Transactional(readOnly = true)
    public NotificationSettingResponse.GetAll getNotificationSetting(Member member) {
        List<NotificationSetting> notificationSettings = notificationSettingRepository.findAllByMember(member);

        return NotificationSettingResponse.GetAll.from(notificationSettings);
    }

    @Transactional
    public void updateNotificationSetting(String notificationType, Member member) {
        NotificationType type = NotificationType.valueOf(notificationType);
        NotificationSettingIds notificationSettingIds = new NotificationSettingIds(member.getId(), type);

        NotificationSetting notificationSetting = notificationSettingRepository.findById(notificationSettingIds)
                .orElseThrow(() -> new RuntimeException("알림 설정이 존재하지 않습니다."));

        notificationSetting.updateReceive();

        notificationSettingRepository.save(notificationSetting);
    }

    public void createNotificationSetting(Member member) {
        for (NotificationType type : NotificationType.values()) {
            NotificationSetting notificationSetting = NotificationSetting.builder()
                    .member(member)
                    .notificationType(type)
                    .isReceive(true)
                    .build();

            notificationSettingRepository.save(notificationSetting);
        }
    }


}
