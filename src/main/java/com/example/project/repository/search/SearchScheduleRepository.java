package com.example.project.repository.search;

import com.example.project.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchScheduleRepository {
  Page<Schedule> searchPage(String keyword, Pageable pageable);
}
