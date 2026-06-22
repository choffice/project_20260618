package com.example.project.repository;

import com.example.project.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
  // 이메일로 유저 찾기
  @EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
  Optional<User> findByEmail(String email);

  // 이미 존재하는 이메일인지 확인
  boolean existsByEmail(String email);

  // 이미 존재하는 이름인지 확인
  boolean existsByName(String name);

  // 이메일 검색으로 유저 목록 조회
  List<User> findByEmailContainingIgnoreCase(String email);
}
