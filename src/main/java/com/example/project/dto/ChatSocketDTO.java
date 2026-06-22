package com.example.project.dto;

//오창 20260622

public class ChatSocketDTO{
  private Long sid, uid;
  private boolean reqCode;

  public ChatSocketDTO() {}

  public ChatSocketDTO(Long sid, Long uid, boolean reqCode) {
    this.sid = sid;
    this.uid = uid;
    this.reqCode = reqCode;
  }

   public Long getSid() {
    return sid;
  }

  public void setSid(Long sid) {
    this.sid = sid;
  }

     public Long getUid() {
    return uid;
  }

  public void setUid(Long uid) {
    this.sid = uid;
  }

  public boolean isReqCode() {
    return reqCode;
  }

  public void setReqCode(boolean reqCode) {
    this.reqCode = reqCode;
  }
}