package com.example.codebase.domain.notification.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.notification.entity.Notification;
import com.example.codebase.domain.notification.entity.NotificationWithIsRead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n AS notification, ns.isRead AS isRead " +
            "FROM Notification n " +
            "LEFT JOIN NotificationReceivedStatus ns " +
            "ON n = ns.notification " +
            "WHERE ns.member = :member " +
            "ORDER BY ns.isRead ASC, n.createdTime DESC")
    Page<NotificationWithIsRead> findByMember(Member member, PageRequest pageRequest);

}
