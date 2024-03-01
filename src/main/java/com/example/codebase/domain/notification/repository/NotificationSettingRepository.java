package com.example.codebase.domain.notification.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, UUID> {

    default boolean findByMemberAndNotificationType(Member targetMember, NotificationType type){
        return switch (type) {
            case NEW_FOLLOWER -> findByMemberAndReceiveNewFollow(targetMember);
            case MENTION -> findByMemberAndReceiveMention(targetMember);
            case UPDATE -> findByMemberAndReceiveUpdate(targetMember);
            case ANNOUNCEMENT -> findByMemberAndReceiveAnnouncement(targetMember);
            case PROMOTIONAL_NEWS -> findByMemberAndReceivePromotionalNews(targetMember);
        };
    }

    @Query("SELECT n.receiveNewFollower FROM NotificationSetting n WHERE n.member = :targetMember")
    boolean findByMemberAndReceiveNewFollow(Member targetMember);

    @Query("SELECT n.receiveMention FROM NotificationSetting n WHERE n.member = :targetMember")
    boolean findByMemberAndReceiveMention(Member targetMember);

    @Query("SELECT n.receiveUpdate FROM NotificationSetting n WHERE n.member = :targetMember")
    boolean findByMemberAndReceiveUpdate(Member targetMember);

    @Query("SELECT n.receiveAnnouncement FROM NotificationSetting n WHERE n.member = :targetMember")
    boolean findByMemberAndReceiveAnnouncement(Member targetMember);

    @Query("SELECT n.receivePromotionalNews FROM NotificationSetting n WHERE n.member = :targetMember")
    boolean findByMemberAndReceivePromotionalNews(Member targetMember);

    default List<Member> findMemberByColumnType(NotificationType columnNotificationType){
        return switch (columnNotificationType) {
            case NEW_FOLLOWER -> findMembersByReceiveNewFollow();
            case MENTION -> findMembersByReceiveMention();
            case UPDATE -> findMembersByReceiveUpdate();
            case ANNOUNCEMENT -> findMembersByReceiveAnnouncement();
            case PROMOTIONAL_NEWS -> findMembersByReceivePromotionalNews();
        };
    }

    @Query("SELECT n.member FROM NotificationSetting n WHERE n.receiveNewFollower = true")
    List<Member> findMembersByReceiveNewFollow();

    @Query("SELECT n.member FROM NotificationSetting n WHERE n.receiveMention = true")
    List<Member> findMembersByReceiveMention();

    @Query("SELECT n.member FROM NotificationSetting n WHERE n.receiveUpdate = true")
    List<Member> findMembersByReceiveUpdate();

    @Query("SELECT n.member FROM NotificationSetting n WHERE n.receiveAnnouncement = true")
    List<Member> findMembersByReceiveAnnouncement();

    @Query("SELECT n.member FROM NotificationSetting n WHERE n.receivePromotionalNews = true")
    List<Member> findMembersByReceivePromotionalNews();
}
