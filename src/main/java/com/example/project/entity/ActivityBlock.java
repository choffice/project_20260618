package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActivityBlock extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bid;

  // 활동이 소속된 플래너즈
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "planners_id", nullable = false)
  private Planners planners;

  // 활동 이름
  @Column(nullable = false, length = 50)
  private String name;
}
