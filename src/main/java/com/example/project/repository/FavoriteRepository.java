package com.example.project.repository;

import com.example.project.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
  // 유저 아이디로 찜한 플래너즈 찾기
  @Query("select f.planners.tid from Favorite f where f.user.uid = :uid")
  List<Long> findFavoritePlannersIdsByUserId(@Param("uid") Long uid);

  // 찜하기 삭제
  @Query("delete from Favorite f where f.user.uid = :uid and f.planners.tid = :tid")
  void deleteFavoriteByUserIdAndPlannersId(@Param("uid") Long uid, @Param("tid") Long tid);

  // 유저 아이디와 플래너즈 아이디로 찜하기 조회
  @Query("select f from Favorite f where f.user.uid = :uid and f.planners.tid = :tid")
  Optional<Favorite> findByUidAndTid(@Param("uid") Long uid, @Param("tid") Long tid);

  // 해당 찜하기가 존재하는지 여부 확인
  @Query("select count(f) > 0 from Favorite f where f.user.uid = :uid and f.planners.tid = :tid")
  boolean existsByUidAndTid(@Param("uid") Long uid, @Param("tid") Long tid);
}
