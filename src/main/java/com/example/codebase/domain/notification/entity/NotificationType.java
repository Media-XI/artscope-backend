package com.example.codebase.domain.notification.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum NotificationType {
    MENTION("언급", "${username} 님이 당신을 언급했습니다."),
    UPDATE("업데이트", "${username} 님의 글이 업데이트되었습니다."),
    ANNOUNCEMENT("공지사항", "새로운 공지사항이 있습니다"),
    NEW_FOLLOWER("새로운 팔로워", "${username} 님이 당신을 팔로우하기 시작했습니다."),
    PROMOTIONAL_NEWS("홍보 소식", "새로운 홍보 소식이 있습니다.");

    private final String title;
    private final String description;

    private static final Map<String, NotificationType> TYPE_MAP=
            Stream.of(values()).collect(Collectors.toMap(Enum::name, notificationType -> notificationType));

    NotificationType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void validAdminNotificationType() {
        if (this.equals(MENTION) || this.equals(NEW_FOLLOWER)) {
            throw new RuntimeException("해당 알림 타입은 전체알림으로 발송할 수 없습니다. [" + adminNotificationTypes() + "]에서 입력해주세요.");
        }
    }

    private String adminNotificationTypes() {
        return Arrays.stream(NotificationType.values())
                .filter(notificationType -> !notificationType.equals(MENTION) && !notificationType.equals(NEW_FOLLOWER))
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    public static NotificationType fromString(String type) {
        NotificationType result = TYPE_MAP.get(type);
        if (result == null) {
            throw new IllegalArgumentException("잘못된 알림 타입입니다. [" + allNotificationTypes() + "]에서 입력해주세요.");
        }
        return result;
    }

    public static String allNotificationTypes() {
        return Arrays.stream(NotificationType.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
