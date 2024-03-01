package com.example.codebase.domain.notification.service;


import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.notification.entity.Notification;
import com.example.codebase.domain.notification.entity.NotificationMessageFormatter;
import com.example.codebase.domain.notification.entity.NotificationType;
import com.example.codebase.domain.notification.repository.NotificationRepository;
import com.example.codebase.domain.notification.repository.NotificationSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationSendServiceImpl implements NotificationSendService {

    private final NotificationRepository notificationRepository;

    private final NotificationSettingRepository notificationSettingRepository;

    private final NotificationEventService notificationEventService;

    private final MemberService memberService;

    @Autowired
    public NotificationSendServiceImpl(NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, NotificationEventService notificationEventService, MemberService memberService) {
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.notificationEventService = notificationEventService;
        this.memberService = memberService;
    }

    @Override
    public void send(Member loginMember, Member targetMember, NotificationType type) {
        this.sendNotification(loginMember, targetMember, type);
    }

    @Override
    public void send(String loginMember, String targetMember, NotificationType type) {
        Member login = memberService.getEntity(loginMember);
        Member target = memberService.getEntity(targetMember);
        this.sendNotification(login, target, type);
    }

    private void sendNotification(Member loginMember, Member targetMember, NotificationType type) {
        if (!checkSendSetting(targetMember, type)) {
            return;
        }

        String message = NotificationMessageFormatter.formatMessage(type, loginMember.getUsername());
        Notification notification = notificationRepository.save(Notification.of(targetMember, message, type));

        notificationEventService.sendNotification(targetMember, notification);
    }

    private boolean checkSendSetting(Member targetMember, NotificationType type) {
        return notificationSettingRepository.findByMemberAndNotificationType(targetMember, type);
    }

    @Override
    public void send(List<Member> members, Notification notification) {
        notificationEventService.sendAllNotification(members, notification);
    }

    @Override
    public void incrementNotificationCount(Member member) {
        notificationEventService.upDateCountAndSend(member, 1);
    }

    @Override
    public void resetAndSendNotificationCount(Member member) {
        notificationEventService.resetAndSendNotificationCount(member);
    }

    @Override
    public void decrementNotificationCount(Member member) {
        notificationEventService.upDateCountAndSend(member, -1);

    }

    @Override
    public void decrementNotificationCount(Member member, int size) {
        notificationEventService.upDateCountAndSend(member, -size);

    }
}


