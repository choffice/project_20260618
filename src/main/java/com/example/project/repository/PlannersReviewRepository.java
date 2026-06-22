package com.example.project.repository;

import com.example.project.entity.PlannersReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlannersReviewRepository extends JpaRepository<PlannersReview, Long> {
  
  // planners.tid가 특정 tid인 리뷰들을 regDate 최신순으로 가져오는 메서드
  List<PlannersReview> findByPlanners_TidOrderByRegDateDesc(Long tid);  

  // 해당 사용자가 해당 플래너즈에 리뷰를 작성하였는지 여부 조회
  boolean existsByPlanners_TidAndUser_Uid(Long tid, Long uid);
}
