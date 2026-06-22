package com.example.project.service;

import java.util.List;

import com.example.project.dto.ActivityBlockDTO;
import com.example.project.entity.ActivityBlock;

public interface ActivityBlockService {
  List<ActivityBlockDTO> getActivityBlockList(Long tid);

  Long addActivityBlock(Long tid, String name);

  Long removeActivityBlock(Long bid);

  default ActivityBlockDTO entityToDto(ActivityBlock activityBlock) {
    ActivityBlockDTO activityBlockDTO = ActivityBlockDTO.builder()
        .bid(activityBlock.getBid())
        .planners(activityBlock.getPlanners().getName())
        .name(activityBlock.getName())
        .regDate(activityBlock.getRegDate())
        .modDate(activityBlock.getModDate())
        .build();

    return activityBlockDTO;
  }
}
