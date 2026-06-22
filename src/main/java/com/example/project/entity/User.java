package com.example.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long uid;

  // 이메일
  @Column(nullable = false, unique = true, length = 100)
  private String email;

  // 비밀번호
  @Column(nullable = false, length = 255)
  private String password;

  // 닉네임
  @Column(nullable = false, unique = true, length = 30)
  private String name;

  // 프로필 이미지
  private String profileImg;

  // 거주 지역
  private String location;

  // 선호 카테고리
  private String category;

  // 권한
  @ElementCollection(fetch = FetchType.LAZY)
  @Builder.Default
  private Set<UserRole> roleSet = new HashSet<>();

  // 계정 상태
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status;

  public void changePassword(String password) {
    this.password = password;
  }

  public void changeName(String name) {
    this.name = name;
  }

  public void changeProfileImg(String profileImg) {
    this.profileImg = profileImg;
  }

  public void changeLocation(String location) {
    this.location = location;
  }

  public void changeCategory(String category) {
    this.category = category;
  }

  public void addMemberRole(UserRole role) {
    roleSet.add(role);
  }

  public void changeStatus(UserStatus status) {
    this.status = status;
  }
}
