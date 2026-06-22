package com.example.project.dto;

import com.example.project.entity.ScheduleParticipant;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleParticipantDTO {
  private Long pid, uid, sid;
  private String nickname, profileImage;
}