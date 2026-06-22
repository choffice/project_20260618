package com.example.project.service;

import com.example.project.entity.Notification;
import com.example.project.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;

  @Override
  public List<Notification> getNotifications(Long uid) {
    return notificationRepository.findNotificationsByUserId(uid);
  }

  @Override
  public int getNotificationsCount(Long uid) {
    return notificationRepository.findNotificationsCount(uid);
  }

  @Override
  public void removeNotification(Long nid) {
    notificationRepository.deleteById(nid);
  }

  @Override
  @Transactional
  public void markAsRead(Long nid) {
    Notification notification = notificationRepository.findById(nid)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
    notification.markAsRead();
    notificationRepository.save(notification);
  }
}
