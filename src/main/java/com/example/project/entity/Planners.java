package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"owner"})
public class Planners extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tid;

  // 운영자
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  // 이름
  @Column(nullable = false, length = 50)
  private String name;

  // 설명
  private String description;

  // 인원 제한
  @Column(nullable = false)
  private int maxPopulation;

  // 현재 인원 수
  @Column(nullable = false)
  private int population;

  // 썸네일
  private String plannersThumbnail;

  // 배너
  private String plannersBanner;

  // 공개 여부
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlannersStatus status;

  // 지역
  @Column(nullable = false, length = 50)
  private String location;

  // 카테고리
  @Column(nullable = false, length = 20)
  private String category;

  @OneToMany(mappedBy = "planners", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PlannersUser> plannersUsers = new ArrayList<>();

  @OneToMany(mappedBy = "planners", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Schedule> schedules = new ArrayList<>();

  @OneToMany(mappedBy = "planners", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PlannersInvitation> plannersInvitations = new ArrayList<>();

  @OneToMany(mappedBy = "planners", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Favorite> favorites = new ArrayList<>();

  // 가입 양식
  @Column(columnDefinition = "TEXT")
  private String formSchema;

  public void increasePopulation() {
    this.population++;
  }

  public void decreasePopulation() {
    this.population--;
  }

  public void changeDescription(String description) {
    this.description = description;
  }

  public void changeMaxPopulation(int change) {
    this.maxPopulation = change;
  }

  public void changeStatus(PlannersStatus status) {
    this.status = status;
  }

  public void changeThumbnail(String plannersThumbnail) {
    this.plannersThumbnail = plannersThumbnail;
  }

  public void changeBanner(String plannersBanner) {
    this.plannersBanner = plannersBanner;
  }

  public void changeFormSchema(String formSchema) {
    this.formSchema = formSchema;
  }
}
