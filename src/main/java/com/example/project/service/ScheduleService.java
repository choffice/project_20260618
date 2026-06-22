package com.example.project.service;

import com.example.project.dto.*;
import com.example.project.entity.Planners;
import com.example.project.entity.Schedule;
import com.example.project.entity.ScheduleRole;
import com.example.project.entity.ScheduleStatus;
import com.example.project.entity.ScheduleParticipant;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ScheduleService {

  /*플래너즈 안에서 일정 목록 조회*/
  PageResultDTO<ResponseScheduleDTO, Schedule> getScheduleList(Long tid, Long uid, PageRequestDTO pageRequestDTO, String filterStatus);
    // PageResultDTO<ResponseScheduleDTO, Schedule> getScheduleList(Long tid, Long uid, PageRequestDTO pageRequestDTO);
  /*일정 등록*/
  Long createSchedule(Long tid, Long uid, RegisterScheduleDTO registerScheduleDTO, MultipartFile scheduleThumbnail, String uploadPath) throws IOException;

  ResponseScheduleDTO get(Long sid, Long uid);

  boolean isJoiner(Long sid, Long uid);

  boolean isCreator(Long sid, Long uid);

  Long submitSchedule(Long sid, List<ScheduleBlockDTO> scheduleBlockDTO);

  Long removeSchedule(Long sid, Long uid);

  List<ScheduleBlockDTO> getScheduleBlockList(Long sid);

  List<ScheduleParticipantDTO> getScheduleParticipantsList(Long sid);

  default Schedule dtoToEntity(RegisterScheduleDTO registerScheduleDTO) {
    return Schedule.builder()
        .title(registerScheduleDTO.getTitle())
        .description(registerScheduleDTO.getDescription())
        .maxPopulation(registerScheduleDTO.getMaxPopulation())
        .population(1)
        .scheduleThumbnail(registerScheduleDTO.getScheduleThumbnail())
        .startDate(registerScheduleDTO.getStartDate())
        .endDate(registerScheduleDTO.getEndDate())
        .build();
  }

  default ResponseScheduleDTO entityToDto(Schedule schedule) {
    return ResponseScheduleDTO.builder()
        .sid(schedule.getSid())
        .plannersName(schedule.getPlanners().getName())
        .creator(schedule.getCreator().getName())
        .creatorUid(schedule.getCreator().getUid())
        .creatorProfile(schedule.getCreator().getProfileImg())
        .title(schedule.getTitle())
        .description(schedule.getDescription())
        .maxPopulation(schedule.getMaxPopulation())
        .population(schedule.getPopulation())
        .scheduleThumbnail(schedule.getScheduleThumbnail())
        .status(schedule.getScheduleStatus())
        .startDate(schedule.getStartDate())
        .endDate(schedule.getEndDate())
        .regdate(schedule.getRegDate())
        .userStatus(ScheduleRole.NONE)
        .build();
  }
}
