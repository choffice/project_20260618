package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Friend extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ffid;

  // 친구 요청한 사용자
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "request_userid", nullable = false)
  private User requester;

  // 요청을 받은 사용자
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receive_userid", nullable = false)
  private User receiver;

  // 친구 추가 상태
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FriendStatus status;

  public void changeStatus(FriendStatus status) {
    this.status = status;
  }
}
