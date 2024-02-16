package com.example.codebase.domain.notification.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.entity.NotificationReceivedStatus;
import com.example.codebase.domain.notification.entity.NotificationReceivedStatusIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationReceivedStatusRepository extends JpaRepository<NotificationReceivedStatus, NotificationReceivedStatusIds> {

    @Query("SELECT nr FROM NotificationReceivedStatus nr WHERE nr.member= :member AND nr.isRead = false")
    List<NotificationReceivedStatus> findByMember(Member member);

    @Query("SELECT COUNT(nr) FROM NotificationReceivedStatus nr WHERE nr.member= :member AND nr.isRead = false")
    int countByMember(Member member);


    @Query("SELECT nr FROM NotificationReceivedStatus nr WHERE nr.member= :member")
    List<NotificationReceivedStatus> findAllNotificationByMember(Member member);
}
