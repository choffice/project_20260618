package com.example.project.dto;

import com.example.project.entity.PlannersStatus;
import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPlannersDTO {
  private Long tid;
  private String name;
  private String description;
  private int maxPopulation;
  private int population;
  private String plannersThumbnail;
  private String plannersBanner;
  private PlannersStatus status;
  private String location;
  private String category;
}
