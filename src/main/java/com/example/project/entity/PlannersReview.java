package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"planners", "user"})
public class PlannersReview extends BasicEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long rid; // 리뷰 번호

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "planners_id", nullable = false)
  private Planners planners; // 어떤 플래너즈의 리뷰인지

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // 리뷰 작성자

  @Column(nullable = false, length = 100)
  private String title; // 리뷰 제목

  @Column(nullable = false)
  private Integer rating; // 별점

  @Column(nullable = false, length = 1000)
  private String content; // 리뷰 내용

  public void changeReview(String title, Integer rating, String content){
    // 수정할 때 제목, 별점, 내용 바꿈
  this.title = title;
  this.rating = rating;
  this.content = content;
  }
}


