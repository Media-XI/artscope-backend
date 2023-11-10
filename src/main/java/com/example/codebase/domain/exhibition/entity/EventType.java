package com.example.codebase.domain.exhibition.entity;

public enum EventType {
    EXHIBITION,
    LECTURE,
    WORKSHOP,
    SPECIAL,
    CONCERT,
    STANDARD;

    public static EventType create(String eventType) {
        switch (eventType) {
      case "EXHIBITION":
        return EXHIBITION;
      case "LECTURE":
        return LECTURE;
      case "WORKSHOP":
        return WORKSHOP;
      case "SPECIAL":
        return SPECIAL;
      case "CONCERT":
        return CONCERT;
      case "ALL":
        return null;
      default:
        return STANDARD;
    }
    }
}
