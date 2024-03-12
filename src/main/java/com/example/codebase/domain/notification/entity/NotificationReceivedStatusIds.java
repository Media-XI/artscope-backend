package com.example.codebase.domain.notification.entity;

import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReceivedStatusIds implements Serializable {

    private Long notification;

    private UUID member;

}
