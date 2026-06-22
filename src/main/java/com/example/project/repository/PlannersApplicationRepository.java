package com.example.project.repository;

import com.example.project.entity.PlannersApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlannersApplicationRepository extends JpaRepository<PlannersApplication, Long> {
  @Query("select pa from PlannersApplication pa where pa.planners.tid = :tid and pa.status = 'PENDING'")
  List<PlannersApplication> findByPlanners_Tid(@Param("tid") Long tid);

  // 해당 유저가 해당 플래너즈에 이미 가입 신청을 넣은 상태인지 여부 조회
  @Query("select count(pa) > 0 from PlannersApplication pa where pa.planners.tid = :tid and pa.user.uid = :uid")
  boolean isSubmitted(@Param("tid") Long tid, @Param("uid") Long uid);

  // 플래너즈 id와 유저 id로 가입신청 기록 삭제
  void deleteByPlanners_TidAndUser_Uid(Long tid, Long uid);
}
