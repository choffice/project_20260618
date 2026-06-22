package com.example.project.repository;

import com.example.project.entity.Planners;
import com.example.project.entity.User;
import com.example.project.repository.search.SearchPlannersRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlannersRepository extends JpaRepository<Planners, Long>, SearchPlannersRepository {
  boolean existsByName(String name);  // 이미 존재하는 플래너즈 이름인지 여부 조회

  // 최근 1달간 가장 가입자 수가 많은 플래너즈 5개 조회
  @Query(value = """
    select p.*
    from planners p
    join planners_user pu
        on p.tid = pu.planners_id
    where pu.regDate >= date_sub(now(), interval 1 month)
    and p.status = 'PUBLIC'
    group by p.tid
    order by count(pu.user_id) desc
    limit 5
    """, nativeQuery = true)
  List<Planners> findPopularPlanners();   

  // 추천 플래너즈 5개 조회
  @Query(value = "select p.* from planners p join user u on p.location = u.location and p.category = u.category where u.uid = :uid and p.status = 'PUBLIC' limit 5", nativeQuery = true)
  List<Planners> findRecommendedPlanners(@Param("uid") Long uid);   

  // 내가 가입한 플래너즈 목록 조회
  @Query(value = "select p.* from planners p join planners_user pu on p.tid = pu.planners_id where pu.user_id = :uid", nativeQuery = true)
  List<Planners> findMyPlanners(@Param("uid") Long uid);  

}
