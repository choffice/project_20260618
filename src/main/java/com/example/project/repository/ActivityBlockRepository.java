package com.example.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.project.entity.ActivityBlock;

public interface ActivityBlockRepository extends JpaRepository<ActivityBlock, Long> {
  // 플래너즈 id로 활동 블럭 조회
  @Query("select a from ActivityBlock a where a.planners.tid = :tid")
  List<ActivityBlock> getActivityBlockList(@Param("tid") Long tid);
}
