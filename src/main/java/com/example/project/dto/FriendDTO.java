package com.example.project.dto;

import com.example.project.entity.FriendStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendDTO {
  private Long ffid;
  private Long uid;
  private String name;
  private String email;
  private String profileImg;
  private FriendStatus status;
}