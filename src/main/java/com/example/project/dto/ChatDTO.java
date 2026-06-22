package com.example.project.dto;

//오창 sid 추가 20260618

public class ChatDTO{
  private String sender, content, userProfile;
  private Long sid, senderId;

  public ChatDTO() {}

  public ChatDTO(Long sid, String sender, String content, Long senderId) {
    this.sid = sid;
    this.sender = sender;
    this.content = content;
    this.senderId = senderId;
    this.userProfile = userProfile;
  }

  public Long getSid() { return sid; }

  public void setSid(Long sid) {
    this.sid = sid;
  }

  public String getSender() { return sender; }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) { this.content = content; }

  public Long getSenderId() {
    return senderId;
  }

  public void setSenderId(Long senderId) {
    this.senderId = senderId;
  }

  public String getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(String userProfile) { this.userProfile = userProfile; }

}