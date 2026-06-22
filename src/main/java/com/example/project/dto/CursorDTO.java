package com.example.project.dto;

public class CursorDTO {
  private Long sid;//소켓 세션 구분을 위해
  private Long userId;
  private String username;
  private int x;
  private int y;

  public CursorDTO() {}

  //오창20260617 0932
  public Long getSid() { return sid; }
  public void setSid(Long sid) { this.sid = sid; }
  //

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public int getX() { return x; }
  public void setX(int x) { this.x = x; }

  public int getY() { return y; }
  public void setY(int y) { this.y = y; }
}