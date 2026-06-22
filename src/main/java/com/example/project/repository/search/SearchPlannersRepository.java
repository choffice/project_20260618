package com.example.project.repository.search;

import com.example.project.entity.Planners;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPlannersRepository {
  // SearchBoardRepository: 복수개의 엔티티를 검색하기 위해 별도의 interface로 분리
  Page<Planners> searchPage(String keyword, String location, String category, Pageable pageable);

  Page<Planners> searchMyPlannersPage(String keyword, String location, String category, Long uid, Pageable pageable);

  Page<Planners> searchInvitedPlannersPage(String keyword, String location, String category, Long uid, Pageable pageable);

  Page<Planners> searchCreatedPlannersPage(String keyword, String location, String category, Long uid, Pageable pageable);

  Page<Planners> searchFavoritePlannersPage(String keyword, String location, String category, Long uid, Pageable pageable);
}
