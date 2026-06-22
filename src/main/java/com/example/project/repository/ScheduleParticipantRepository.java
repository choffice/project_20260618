package com.example.project.repository;

import com.example.project.entity.ScheduleParticipant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long> {
  boolean existsBySchedule_SidAndUser_Uid(Long sid, Long uid);

  List<ScheduleParticipant> findBySchedule_Sid(Long sid);
}
