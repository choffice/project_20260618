package com.example.project.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityBlockDTO {
  private Long bid;
  private String planners;
  private String name;
  private LocalDateTime regDate, modDate;
}
