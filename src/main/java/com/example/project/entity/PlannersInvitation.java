package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlannersInvitation extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long vid;

  // 초대하는 플래너즈
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "planners_id", nullable = false)
  private Planners planners;

  // 초대하는 사람
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inviter_id", nullable = false)
  private User inviter;

  // 초대받는 사람
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invitee_id", nullable = false)
  private User invitee;

  // 초대 상태
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InvitationStatus status;
}
