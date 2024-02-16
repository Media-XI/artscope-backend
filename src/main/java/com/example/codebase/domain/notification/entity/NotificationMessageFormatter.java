package com.example.codebase.domain.notification.entity;

import com.example.codebase.domain.notification.dto.MessageRequest;

public class NotificationMessageFormatter {

    public static String formatMessage(NotificationType type) {
        String title = type.getTitle();
        String content = type.getDescription();

        return String.format("{\"title\": \"%s\", \"content\": \"%s\"}", title, content);
    }

    public static String formatMessage(NotificationType type, String username) {
        String title = type.getTitle();
        String content = type.getDescription().replace("${username}", username);

        return String.format("{\"title\": \"%s\", \"content\": \"%s\"}", title, content);
    }

    public static String formatMessage(MessageRequest messageRequest) {
        String title = messageRequest.getTitle();
        String message = messageRequest.getMessage();
        return String.format("{\"title\": \"%s\", \"content\": \"%s\"}", title, message);
    }
}
