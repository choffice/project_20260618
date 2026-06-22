package com.example.project.service;

import java.util.List;

public interface FavoriteService {
  Long add(Long uid, Long tid);

  List<Long> get(Long uid);

  void remove(Long uid, Long tid);
}
