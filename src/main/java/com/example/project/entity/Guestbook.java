package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"planners", "writer"})
public class Guestbook extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long gid;

  // 방명록이 소속된 플래너즈
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "planners_id", nullable = false)
  private Planners planners;

  // 방명록을 추가한 유저
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "writer_id", nullable = false)
  private User writer;

  // 방명록 내용
  @Column(nullable = false, length = 1000)
  private String content;

  public void changeContent(String content) {
    this.content = content;
  }
}
