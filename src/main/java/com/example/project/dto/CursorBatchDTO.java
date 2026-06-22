package com.example.project.dto;

import java.util.List;

public class CursorBatchDTO {
  private Long sid;//소켓 세션 구분을 위해
  private List<CursorDTO> cursors;

  public CursorBatchDTO() {
  }

  //오창20260617 0932
  public Long getSid() { return sid; }
  public void setSid(Long sid) { this.sid = sid; }
  //

  public List<CursorDTO> getCursors() {
    return cursors;
  }

  public void setCursors(List<CursorDTO> cursors) {
    this.cursors = cursors;
  }
}