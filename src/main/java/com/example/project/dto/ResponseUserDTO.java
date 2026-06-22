package com.example.project.dto;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserDTO {
  private Long uid;
  private String email;
  private String name;
  private String profileImg;
  private String location;
  private String category;
}
