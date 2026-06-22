package com.example.project.dto;

public class ChatStatusDTO {
  private String chatterName, chatterLoc, profileImg;
  private Long chatterId;
  private int status, reqCode;

  public ChatStatusDTO() {}

  public ChatStatusDTO(Long chatterId, String chatterName, String profileImg, String chatterLoc, int status, int reqCode) {
    this.chatterName = chatterName;
    this.chatterLoc = chatterLoc;
    this.chatterId = chatterId;
    this.status = status;
    this.profileImg = profileImg;
    this.reqCode = reqCode;
  }

  public int getReqCode() {
    return reqCode;
  }

  public void setReqCode(int reqCode) {
    this.reqCode = reqCode;
  }

  public void setProfileImg(String profileImg) {
    this.profileImg = profileImg;
  }

  public String getProfileImg() {
    return profileImg;
  }

  public String getChatterName() {
    return chatterName;
  }

  public void setChatter(String chatterName) {
    this.chatterName = chatterName;
  }

  public String getChatterLoc() {
    return chatterLoc;
  }

  public void setChatterLoc(String chatterLoc) {
    this.chatterLoc = chatterLoc;
  }

  public Long getChatterId() {
    return chatterId;
  }

  public void setChatterId(Long chatterId) {
    this.chatterId = chatterId;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}