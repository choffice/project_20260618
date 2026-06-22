package com.example.project.dto;

import com.example.project.entity.PlannersStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePlannersDTO {
  private Long tid;
  private String name;
  private String description;
  private String adminEmail;
  private int population;
  private int maxPopulation;
  private String plannersThumbnail;
  private String plannersBanner;
  private PlannersStatus status;
  private String location;
  private String category;
  private boolean favorite;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
