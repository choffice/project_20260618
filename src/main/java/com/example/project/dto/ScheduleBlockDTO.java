package com.example.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class ScheduleBlockDTO {
  private Long bid;
  private LocalDate date;
  private LocalTime startTime, endTime;
  private String name, dateStr, startTimeStr, endTimeStr;
}
