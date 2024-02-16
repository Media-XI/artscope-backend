package com.example.codebase.domain.notification.service;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.dto.NotificationResponse;
import com.example.codebase.domain.notification.entity.Notification;
import com.example.codebase.domain.notification.repository.NotificationReceivedStatusRepository;
import com.example.codebase.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationEventService {

    private final RedisUtil redisUtil;

    private final NotificationReceivedStatusRepository notificationReceivedStatusRepository;

    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    private static final long SSE_SESSION_TIMEOUT = 1000 * 60 * 30;
    private static final long REDIS_EXPIRE_DURATION = 1000 * 60 * 30;

    public NotificationEventService(NotificationReceivedStatusRepository notificationReceivedStatusRepository, RedisUtil redisUtil) {
        this.notificationReceivedStatusRepository = notificationReceivedStatusRepository;
        this.redisUtil = redisUtil;
    }

    @Transactional
    public SseEmitter connection(Member member) {
        int count = notificationReceivedStatusRepository.countByMember(member);
        NotificationResponse.EventMessage eventMessage = NotificationResponse.EventMessage.from(null, count, null, LocalDateTime.now().toString());

        registerUserConnection(member, eventMessage);

        SseEmitter emitter = new SseEmitter(SSE_SESSION_TIMEOUT);

        emitter.onTimeout(() -> {
            log.info("onTimeOut callback");
            emitter.complete(); // 시간 초과 발생시 -> Complete 호출
        });

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            userEmitters.remove(member.getUsername());
        });

        userEmitters.put(member.getUsername(), emitter);

        try {
            emitter.send(SseEmitter.event().name("notificaiton_connection").data(eventMessage));
            log.info("SSE 연결 성공: {}", member.getUsername());
            return emitter;
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }
    }

    private void registerUserConnection(Member member, NotificationResponse.EventMessage eventMessage) {
        String key = getNotificationKey(member);
        redisUtil.getData(key).ifPresentOrElse(json -> {
               // JSON이 존재하는 경우, 업데이트
                int count = notificationReceivedStatusRepository.countByMember(member);
                eventMessage.setCount(count);
                redisUtil.saveNotificationMessage(key, eventMessage, REDIS_EXPIRE_DURATION);
        }, () -> {
            // JSON이 존재하지 않는 경우, 저장
            redisUtil.saveNotificationMessage(key, eventMessage, REDIS_EXPIRE_DURATION);
        });
    }

    private void sendSseEvent(String username, NotificationResponse.EventMessage eventMessage) {
        SseEmitter emitter = userEmitters.get(username);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(eventMessage));
                log.info("SSE 알림 전송: {}", username);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }

    public void disconnection(Member member) {
        userEmitters.remove(getNotificationKey(member));
        redisUtil.deleteData(getNotificationKey(member));
    }

    private static String getNotificationKey(Member member) {
        return member.getUsername() + "_notification";
    }


    @Transactional
    public void upDateCountAndSend(Member member, int count) {
        if (userEmitters.containsKey(member.getUsername())) {
            String key = getNotificationKey(member);
            NotificationResponse.EventMessage eventMessage = redisUtil.getNotificationMessage(key);
            eventMessage.setMessage(null);
            eventMessage.setCount(eventMessage.getCount() + count);
            eventMessage.setType(null);
            eventMessage.setSendTime(LocalDateTime.now().toString());
            redisUtil.saveNotificationMessage(key, eventMessage, REDIS_EXPIRE_DURATION);
            sendSseEvent(member.getUsername(), eventMessage);
        }
    }

    @Transactional
    public void sendAllNotification(List<Member> members, Notification notification) {
        members.forEach(member -> {
            sendNotificationIfConnected(member, notification);
        });
    }

    private void sendNotificationIfConnected(Member member, Notification notification) {
        try {
            if (userEmitters.containsKey(member.getUsername())) {
                String key = getNotificationKey(member);
                NotificationResponse.EventMessage eventMessage;
                try {
                    eventMessage = redisUtil.getNotificationMessage(key);
                } catch (RuntimeException e) {
                    log.error("Redis에서 알림 메시지를 가져오는 중 오류 발생: {}", e.getMessage());
                    eventMessage = NotificationResponse.EventMessage.from(null, 0, null, LocalDateTime.now().toString());
                }
                eventMessage.update(notification);
                redisUtil.saveNotificationMessage(key, eventMessage, REDIS_EXPIRE_DURATION);
                sendSseEvent(member.getUsername(), eventMessage);
            }
        } catch (Exception e) {
            log.error("{} 에게 알림 전송 중 오류 발생: {}", member.getUsername(), e.getMessage());
        }
    }

    @Transactional
    public void sendNotification(Member member, Notification notification) {
        sendNotificationIfConnected(member, notification);
    }
}

