package com.example.project.dto;

public class TaskDTO {
  private Long sid;//소켓 세션 구분을 위해
  private Long taskId;
  private String newStatus;

  public TaskDTO() {}

  //오창20260617 0932
  public Long getSid() { return sid; }
  public void setSid(Long sid) { this.sid = sid; }
  //

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public String getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(String newStatus) {
    this.newStatus = newStatus;
  }
}
