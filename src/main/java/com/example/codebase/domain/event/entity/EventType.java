package com.example.codebase.domain.event.entity;

public enum EventType {
    EXHIBITION,
    LECTURE,
    WORKSHOP,
    SPECIAL,
    CONCERT,
    STANDARD;

    public static EventType create(String eventType) {
        return switch (eventType) {
            case "EXHIBITION" -> EXHIBITION;
            case "LECTURE" -> LECTURE;
            case "WORKSHOP" -> WORKSHOP;
            case "SPECIAL" -> SPECIAL;
            case "CONCERT" -> CONCERT;
            case "ALL" -> null;
            default -> STANDARD;
        };
    }
}
