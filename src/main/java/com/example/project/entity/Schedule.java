package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"planners", "creator"})
public class Schedule extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sid;

  // 스케줄이 소속된 플래너즈
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "planners_id", nullable = false)
  @Setter
  private Planners planners;

  // 스케줄을 만든 사람
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @Setter
  private User creator;

  // 스케줄 이름
  @Column(nullable = false, length = 50)
  private String title;

  // 스케줄 설명
  @Column(nullable = false, length = 255)
  private String description;

  // 인원 제한
  @Column(nullable = false)
  private int maxPopulation;

  // 현재 인원 수
  @Column(nullable = false)
  private int population;

  // 썸네일
  private String scheduleThumbnail;

  // 스케줄 완료 상태
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Setter
  private ScheduleStatus scheduleStatus;

  // 스케줄 시작일
  @Column(nullable = false)
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startDate;

  // 스케줄 종료일
  @Column(nullable = false)
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime endDate;

  @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ScheduleParticipant> participants = new ArrayList<>();

  @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ScheduleBlock> scheduleBlocks = new ArrayList<>();
}
