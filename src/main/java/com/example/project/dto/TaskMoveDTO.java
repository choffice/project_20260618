package com.example.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskMoveDTO {
  private Long sid;//소켓 세션 구분을 위해
  private Long userId;
  private String username;
  private Long taskId;
  private String mode;
  private String instanceId;
  private String newStatus;
  private String action;
  private String content;
  private String startTime;
  private String endTime;
  private String scheduleDate;

  public TaskMoveDTO() {}
}
