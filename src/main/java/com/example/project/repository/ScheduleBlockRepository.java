package com.example.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.project.entity.ScheduleBlock;
import com.example.project.entity.ScheduleParticipant;

public interface ScheduleBlockRepository extends JpaRepository<ScheduleBlock, Long> {
  List<ScheduleBlock> findBySchedule_Sid(Long sid);
  
}
