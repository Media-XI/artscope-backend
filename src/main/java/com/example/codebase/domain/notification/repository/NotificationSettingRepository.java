package com.example.codebase.domain.notification.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.notification.entity.NotificationSettingIds;
import com.example.codebase.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, NotificationSettingIds> {

    @Query("SELECT n FROM NotificationSetting n LEFT JOIN Member m ON n.member = m WHERE n.member = :member")
    List<NotificationSetting> findAllByMember(Member member);

    @Query("SELECT m FROM NotificationSetting n LEFT JOIN Member m ON n.member.id = m.id WHERE n.notificationType = :notificationType AND n.isReceive = true")
    List<Member> findMemberByType(NotificationType notificationType);

    @Query("SELECT n FROM NotificationSetting n LEFT JOIN Member m ON n.member = m WHERE n.member = :targetMember AND n.notificationType = :type")
    Optional<NotificationSetting> findByMemberAndNotificationType(Member targetMember, NotificationType type);

}
