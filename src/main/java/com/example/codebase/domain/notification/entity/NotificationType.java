package com.example.codebase.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    MENTION("언급", "${username} 님이 당신을 언급했습니다."),
    UPDATE("업데이트", "${username} 님의 글이 업데이트되었습니다."),
    ANNOUNCEMENT("공지사항", "새로운 공지사항이 있습니다"),
    NEW_FOLLOWER("새로운 팔로워", "${username} 님이 당신을 팔로우하기 시작했습니다."),
    PROMOTIONAL_NEWS("홍보 소식", "새로운 홍보 소식이 있습니다.");

    private final String title;
    private final String description;

    NotificationType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void validAdminNotificationType() {
        if (this.equals(MENTION) || this.equals(NEW_FOLLOWER)) {
            throw new RuntimeException("전체 알림을 보낼 수 없습니다.");
        }
    }

    public static NotificationType fromString(String type){
        for(NotificationType notificationType : NotificationType.values()){
            if(notificationType.name().equals(type)){
                return notificationType;
            }
        }
        throw new IllegalArgumentException("잘못된 알림 타입입니다.");
    }
}
