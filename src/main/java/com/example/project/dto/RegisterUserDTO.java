package com.example.project.dto;

import lombok.Data;

@Data
public class RegisterUserDTO {
  private Long uid;
  private String email;
  private String password;
  private String name;
  private String profileImg;
  private String location;
  private String category;
}
