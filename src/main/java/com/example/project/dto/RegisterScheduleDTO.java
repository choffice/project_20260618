package com.example.project.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterScheduleDTO {
  private String title, description;
  private String scheduleThumbnail;
  private int maxPopulation, population;
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startDate, endDate;
}