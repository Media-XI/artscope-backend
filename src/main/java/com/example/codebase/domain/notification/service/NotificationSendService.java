package com.example.codebase.domain.notification.service;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.entity.Notification;
import com.example.codebase.domain.notification.entity.NotificationType;

import java.util.List;

public interface NotificationSendService {

    void send(Member loginMember,  Member targetMember,NotificationType type);

    void send(String loginMember, String targetMember, NotificationType type);

    void send(List<Member> members, Notification notification);

    void incrementNotificationCount(Member member);

    void resetAndSendNotificationCount(Member member);

    void decrementNotificationCount(Member member);

    void decrementNotificationCount(Member member, int size);
}
