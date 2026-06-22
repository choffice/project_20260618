package com.example.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.project.dto.ActivityBlockDTO;
import com.example.project.entity.ActivityBlock;
import com.example.project.entity.Planners;
import com.example.project.repository.ActivityBlockRepository;
import com.example.project.repository.PlannersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ActivityBlockServiceImpl implements ActivityBlockService {
  private final ActivityBlockRepository activityBlockRepository;
  private final PlannersRepository plannersRepository;

  @Override
  public List<ActivityBlockDTO> getActivityBlockList(Long tid) {
    List<ActivityBlock> list = activityBlockRepository.getActivityBlockList(tid);
    return list.stream().map(this::entityToDto).toList();
  }

  @Override
  public Long addActivityBlock(Long tid, String name) {
    Optional<Planners> planners = plannersRepository.findById(tid);
    
    if (planners.isPresent()) {
      ActivityBlock activityBlock = ActivityBlock.builder()
          .name(name)
          .planners(planners.get())
          .build();
      activityBlockRepository.save(activityBlock);
      return 1L;
    }
    return 0L;
  }

  @Override
  public Long removeActivityBlock(Long bid) {
    Optional<ActivityBlock> activityBlock = activityBlockRepository.findById(bid);

    if (activityBlock.isPresent()) {
      activityBlockRepository.delete(activityBlock.get());
      return 1L;
    }
    return 0L;
  }
}
