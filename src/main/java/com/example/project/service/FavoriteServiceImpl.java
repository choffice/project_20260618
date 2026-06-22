package com.example.project.service;

import com.example.project.entity.Favorite;
import com.example.project.entity.Planners;
import com.example.project.entity.User;
import com.example.project.repository.FavoriteRepository;
import com.example.project.repository.PlannersRepository;
import com.example.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;
  private final PlannersRepository plannersRepository;

  @Override
  public Long add(Long uid, Long tid) {
    Optional<Favorite> existFavorite = favoriteRepository.findByUidAndTid(uid, tid);

    if (existFavorite.isPresent()) {    // 이미 찜한 경우
      favoriteRepository.delete(existFavorite.get());
      return 0L;
    }

    Optional<User> user = userRepository.findById(uid);
    Optional<Planners> planners = plannersRepository.findById(tid);

    if (user.isPresent() && planners.isPresent()) {
      Favorite favorite = Favorite.builder()
          .user(user.get())
          .planners(planners.get())
          .build();
      favoriteRepository.save(favorite);
      return 1L;
    }
    return -1L;
  }

  @Override
  public List<Long> get(Long uid) {
    return favoriteRepository.findFavoritePlannersIdsByUserId(uid);
  }

  @Override
  public void remove(Long uid, Long tid) {
    favoriteRepository.deleteFavoriteByUserIdAndPlannersId(uid, tid);
  }
}
