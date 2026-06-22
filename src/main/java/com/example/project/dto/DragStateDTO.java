package com.example.project.dto;

public class DragStateDTO {
  private Long sid;//소켓 세션 구분을 위해
  private Long userId;
  private String username;
  private Long taskId;
  private String type; // START, END
   private String instanceId;

  public DragStateDTO() {}

  //오창20260617 0932
  public Long getSid() { return sid; }
  public void setSid(Long sid) { this.sid = sid; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public Long getTaskId() { return taskId; }
  public void setTaskId(Long taskId) { this.taskId = taskId; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
}
