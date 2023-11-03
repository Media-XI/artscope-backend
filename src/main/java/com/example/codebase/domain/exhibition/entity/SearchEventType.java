package com.example.codebase.domain.exhibition.entity;

public enum SearchEventType {
  ALL,
  EXHIBITION,
  LECTURE,
  WORKSHOP,
  SPECIAL,
  CONCERT,
  STANDARD;

  public static SearchEventType create(String eventType) {
    switch (eventType) {
      case "ALL":
        return ALL;
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
      default:
        return STANDARD;
    }
  }
}
