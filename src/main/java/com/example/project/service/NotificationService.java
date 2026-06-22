package com.example.project.service;

import com.example.project.entity.Notification;

import java.util.List;

public interface NotificationService {
  List<Notification> getNotifications(Long uid);

  int getNotificationsCount(Long uid);

  void removeNotification(Long nid);

  void markAsRead(Long nid);
}
