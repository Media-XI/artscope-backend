package com.example.codebase.domain.notification.service;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.dto.NotificationSettingResponse;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.notification.entity.NotificationType;
import com.example.codebase.domain.notification.repository.NotificationSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;

    @Autowired
    public NotificationSettingService(NotificationSettingRepository notificationSettingRepository) {
        this.notificationSettingRepository = notificationSettingRepository;
    }

    @Transactional(readOnly = true)
    public NotificationSettingResponse.GetAll getNotificationSetting(Member member) {
        NotificationSetting notificationSetting = notificationSettingRepository.findById(member.getId())
                .orElseThrow(() -> new RuntimeException("알림 설정이 존재하지 않습니다."));

        return NotificationSettingResponse.GetAll.from(notificationSetting);
    }

    @Transactional
    public void updateNotificationSetting(NotificationType type, Member member) {
        NotificationSetting notificationSetting = notificationSettingRepository.findById(member.getId())
                .orElseThrow(() -> new RuntimeException("알림 설정이 존재하지 않습니다."));

        notificationSetting.updateReceive(type);

        notificationSettingRepository.save(notificationSetting);
    }

    @Transactional
    public void createNotificationSetting(Member member) {
        NotificationSetting notificationSetting = NotificationSetting.from(member);
        notificationSettingRepository.save(notificationSetting);
    }
}
