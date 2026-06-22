package com.example.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DragMoveDTO {
  private Long sid;//소켓 세션 구분을 위해
  private Long userId;
  private String username;

  private Long taskId;
  private String instanceId;

  private int x;
  private int y;

  public DragMoveDTO() {
  }
}