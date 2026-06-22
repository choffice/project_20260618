package com.example.project.repository;

import com.example.project.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  // 유저 아이디로 알림 목록 조회
  @Query("select n from Notification n where n.user.uid = :uid order by n.nid desc")
  List<Notification> findNotificationsByUserId(@Param("uid") Long uid);

  // 유저 아이디로 알림 개수 조회
  @Query("select count(n) from Notification n where n.user.uid = :uid and n.isRead = false")
  int findNotificationsCount(@Param("uid") Long uid);
}
