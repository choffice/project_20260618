package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScheduleBlock {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bid;

  // 스케줄에 들어간 활동 블럭
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "block_id", nullable = false)
  private ActivityBlock block;

  // 블럭이 들어간 스케줄
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schedule_id", nullable = false)
  private Schedule schedule;

  // 활동 시작시간
  @Column(nullable = false)
  private LocalDateTime startTime;

  // 활동 종료시간
  @Column(nullable = false)
  private LocalDateTime endTime;
}
