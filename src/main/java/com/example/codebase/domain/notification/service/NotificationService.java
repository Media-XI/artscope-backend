package com.example.codebase.domain.notification.service;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.dto.NotificationMessageRequest;
import com.example.codebase.domain.notification.dto.NotificationResponse;
import com.example.codebase.domain.notification.entity.*;
import com.example.codebase.domain.notification.repository.NotificationReceivedStatusRepository;
import com.example.codebase.domain.notification.repository.NotificationRepository;
import com.example.codebase.domain.notification.repository.NotificationSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;

    private NotificationSettingRepository notificationSettingRepository;

    private NotificationReceivedStatusRepository notificationReceivedStatusRepository;

    private NotificationSendService notificationSendService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, NotificationReceivedStatusRepository notificationReceivedStatusRepository
            , NotificationSendService notificationSendService) {
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.notificationReceivedStatusRepository = notificationReceivedStatusRepository;
        this.notificationSendService = notificationSendService;
    }

    @Transactional
    public void sendNotification(NotificationMessageRequest messageRequest, NotificationType notificationType) {
        String jsonMessage = NotificationMessageFormatter.formatMessage(messageRequest);

        List<Member> members = notificationSettingRepository.findMemberByColumnType(notificationType);

        Notification notification = Notification.of(members, jsonMessage, notificationType);

        notificationRepository.save(notification);
        notificationSendService.send(members, notification);
    }

    @Transactional(readOnly = true)
    public NotificationResponse.GetAll getNotificationList(Member member, PageRequest pageRequest) {
        Page<NotificationWithIsRead> notificationList = notificationRepository.findByMember(member, pageRequest);

        return NotificationResponse.GetAll.from(notificationList);
    }

    @Transactional
    public NotificationResponse.GetAll readAllNotification(Member member) {
        PageRequest pageRequest =  PageRequest.of(0, 20);
        Page<NotificationReceivedStatus> notifications = notificationReceivedStatusRepository.findByMember(member, pageRequest);

        notifications.forEach(NotificationReceivedStatus::read);

        notificationReceivedStatusRepository.saveAll(notifications);

        notificationSendService.decrementNotificationCount(member, notifications.getSize());

        Page<NotificationWithIsRead> notificationList = notificationRepository.findByMember(member, pageRequest);

        return NotificationResponse.GetAll.from(notificationList);
    }

    @Transactional
    public NotificationResponse.Get readNotification(Member member, Long notificationId) {
        NotificationReceivedStatusIds notificationReceivedStatusIds = new NotificationReceivedStatusIds(notificationId, member.getId());

        NotificationReceivedStatus notificationReceivedStatus = notificationReceivedStatusRepository.findById(notificationReceivedStatusIds)
                .orElseThrow(() -> new RuntimeException("해당 알림이 존재하지 않습니다."));

        if (notificationReceivedStatus.isRead()) {
            throw new RuntimeException("이미 읽은 알림입니다.");
        }

        notificationReceivedStatus.read();

        notificationReceivedStatusRepository.save(notificationReceivedStatus);
        notificationSendService.decrementNotificationCount(member);

        return NotificationResponse.Get.from(notificationReceivedStatus.getNotification());
    }

    @Transactional
    public void deleteNotification(Member member, Long notificationId) {
        NotificationReceivedStatusIds notificationReceivedStatusIds = new NotificationReceivedStatusIds(notificationId, member.getId());

        NotificationReceivedStatus notificationReceivedStatus = notificationReceivedStatusRepository.findById(notificationReceivedStatusIds)
                .orElseThrow(() -> new RuntimeException("해당 알림이 존재하지 않습니다."));

        int count = notificationReceivedStatus.isRead() ? 0 : +1;

        notificationReceivedStatus.removeNotification();
        notificationReceivedStatusRepository.delete(notificationReceivedStatus);

        notificationSendService.decrementNotificationCount(member, count);
    }

    @Transactional
    public void deleteAllNotification(Member member) {
        List<NotificationReceivedStatus> notificationReceivedStatuses = notificationReceivedStatusRepository.findAllNotificationByMember(member);

        for (NotificationReceivedStatus notificationReceivedStatus : notificationReceivedStatuses) {
            notificationReceivedStatus.removeNotification();
        }

        notificationReceivedStatusRepository.deleteAll(notificationReceivedStatuses);

        notificationSendService.resetAndSendNotificationCount(member);
    }
}
