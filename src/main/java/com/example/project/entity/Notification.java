package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long nid;

  // 알림 대상
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // 알림 내용
  @Column(nullable = false, length = 100)
  private String title;

  // 알림 종류
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  // 알림 클릭시 이동하는 url
  @Column(length = 255)
  private String url;

  // 알림 읽음 여부
  @Column(columnDefinition = "boolean default false")
  @Builder.Default
  private boolean isRead = false;

  public void markAsRead() {
    this.isRead = true;
  }

}
