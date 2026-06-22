package com.example.project.repository;

import com.example.project.entity.Guestbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {
  Page<Guestbook> findByPlanners_Tid(Long tid, Pageable pageable);
}
