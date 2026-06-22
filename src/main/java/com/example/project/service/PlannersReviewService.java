package com.example.project.service;

import java.util.List;

import com.example.project.entity.PlannersReview;

public interface PlannersReviewService {

// 리뷰 생성
  Long createReview(Long tid, Long uid, String title, Integer rating, String content);

// 리뷰 수정
  void modifyReview(Long rid, Long uid, String reviewTitle, Integer rating, String content);

// 리뷰 삭제
  void deleteReview(Long rid, Long uid);

// 리뷰 목록
  List<PlannersReview> getReviewList(Long tid);

  // 리뷰 존재 여부
  boolean hasReview (Long tid, Long uid);
}