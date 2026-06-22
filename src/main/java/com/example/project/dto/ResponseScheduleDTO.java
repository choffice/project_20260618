package com.example.project.dto;

import com.example.project.entity.Planners;
import com.example.project.entity.ScheduleRole;
import com.example.project.entity.ScheduleStatus;
import com.example.project.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseScheduleDTO {
  private Long sid;
  private String title, description;
  private String creator;
  private Long creatorUid;
  private String creatorProfile;
  private int maxPopulation, population;
  private String scheduleThumbnail;
  private LocalDateTime startDate, endDate;
  private String plannersName;
  private ScheduleStatus status;
  private LocalDateTime regdate;
  //오창 - 참가자 데이터를 목록에서 구분하기 위해 20260618
  private ScheduleRole userStatus; // CREATOR, FOLLOWER, NONE 등
}
