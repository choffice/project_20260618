package com.example.project.controller;

import com.example.project.entity.Notification;
import com.example.project.security.dto.AuthUserDTO;
import com.example.project.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {

  private final NotificationService notificationService;

  // 페이지 이동마다 알림 목록 조회
  @ModelAttribute
  public void getNotifications(Model model, @AuthenticationPrincipal AuthUserDTO user) {
    Long uid = user != null? user.getUid() : null;

    if (user != null) {
      List<Notification> notifications = notificationService.getNotifications(uid);
      int Count = notificationService.getNotificationsCount(uid);

      model.addAttribute("notifications", notifications);
      model.addAttribute("notificationsCount", Count);
    }
  }
}