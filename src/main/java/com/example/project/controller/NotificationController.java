package com.example.project.controller;

import com.example.project.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
  private final NotificationService notificationService;

  // 알림 삭제
  @DeleteMapping("")
  public ResponseEntity<Void> removeNotification(@RequestParam("nid") Long nid) {
    notificationService.removeNotification(nid);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // 알림 읽음 처리 기능
  @PutMapping("/{nid}/read")
  public ResponseEntity<Void> markAsRead(@PathVariable("nid") Long nid) {
    notificationService.markAsRead(nid);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
