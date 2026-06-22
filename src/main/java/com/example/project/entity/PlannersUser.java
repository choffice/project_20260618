package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"planners", "user"})
public class PlannersUser extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mid;

  // 소속된 플래너즈
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "planners_id", nullable = false)
  private Planners planners;

  // 해당 플래너즈에 소속된 유저
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // 플래너즈 내 역할
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlannersRole role;
}
