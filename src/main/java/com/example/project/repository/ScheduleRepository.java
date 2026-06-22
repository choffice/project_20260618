package com.example.project.repository;

import com.example.project.dto.PageRequestDTO;
import com.example.project.entity.PlannersUser;
import com.example.project.entity.Schedule;
import com.example.project.repository.search.SearchScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, SearchScheduleRepository {
  // 그룹아이디로 스케쥴 찾기 쿼리
  @EntityGraph(attributePaths = {"planners"}, type = EntityGraph.EntityGraphType.LOAD)
  Page<Schedule> findByPlanners_Tid(Long tid, Pageable pageable);

  boolean existsByTitle(String title);
  boolean existsBySidAndCreator_Uid(Long sid, Long uid);
}
