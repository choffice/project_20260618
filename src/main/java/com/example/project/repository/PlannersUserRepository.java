package com.example.project.repository;

import com.example.project.entity.PlannersUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlannersUserRepository extends JpaRepository<PlannersUser, Long> {
  // 플래너즈 운영자 조회
  @Query("select pu from PlannersUser pu where pu.planners.tid = :tid and pu.role = 'ADMIN'")
  PlannersUser getOwner(@Param("tid") Long tid);
  
  // 플래너즈에 멤버 목록 조회(페이지)
  @Query("select pu from PlannersUser pu where pu.planners.tid = :tid and pu.role = 'USER'")
  Page<PlannersUser> findByPlanners_Tid(@Param("tid") Long tid, Pageable pageable);
  
  // 플래너즈 멤버 조회
  @Query("select pu from PlannersUser pu where pu.planners.tid = :tid and pu.role = 'USER'")
  List<PlannersUser> getMemberList(@Param("tid") Long tid);

  // 플래너즈에 가입된 멤버 6명 조회
  @Query("select pu from PlannersUser pu where pu.planners.tid = :tid and pu.role <> 'ADMIN'")
  List<PlannersUser> getPreviewMembers(@Param("tid") Long tid, Pageable pageable);

  // 초대받은 사용자가 이미 플래너즈에 가입되어 있는지 확인
  boolean existsByPlanners_TidAndUser_Uid(Long tid, Long uid);

  // 오창 : 플래너즈에 가입한 유저 지우기(20260618)
  Long deletePlannersUserByPlanners_TidAndUser_Uid(Long tid, Long uid);
}
