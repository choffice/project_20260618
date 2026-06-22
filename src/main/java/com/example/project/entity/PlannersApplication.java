package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude={"planners", "user"})
public class PlannersApplication extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 가입 신청한 플래너즈
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "planners_id", nullable = false)
  private Planners planners;

  // 가입 신청한 유저
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // 가입 신청 양식 답변
  @Column(columnDefinition = "TEXT")
  private String answersJson;

  // 가입 신청 상태
  @Enumerated(EnumType.STRING)
  private ApplicationStatus status;
}

