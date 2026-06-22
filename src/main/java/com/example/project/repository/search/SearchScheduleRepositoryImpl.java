package com.example.project.repository.search;

import com.example.project.entity.QSchedule;
import com.example.project.entity.Schedule;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
public class SearchScheduleRepositoryImpl extends QuerydslRepositorySupport implements SearchScheduleRepository {

  public SearchScheduleRepositoryImpl() {
    super(Schedule.class);
  }

  @Override
  public Page<Schedule> searchPage(String keyword, Pageable pageable) {
    // 1) 도메인을 확보
    QSchedule qSchedule = QSchedule.schedule;

    // 2) 도메인을 조인
    JPQLQuery<Schedule> jpqlQuery = from(qSchedule);

    // 3) Tuple 생성: 조인한 객체와 select를 이용해서 필요한 데이터를 tuple로 생성
    JPQLQuery<Schedule> tuple = jpqlQuery.select(qSchedule);

    // 4) 조건절 검색을 위한 검색 객체를 생성
    BooleanBuilder builder = new BooleanBuilder();
    BooleanExpression expression = qSchedule.sid.gt(0l); // 기본 검색 조건
    builder.and(expression); // 필수 검색 조건 지정

    // 5) 검색 조건 추가
    if (keyword != null && !keyword.isEmpty()) {
      BooleanBuilder keywordCondition = new BooleanBuilder();
      keywordCondition.or(qSchedule.title.containsIgnoreCase(keyword));
      builder.and(keywordCondition);
    }

    // 6) 조인된 tuple에 추가된 조건절 적용
    tuple.where(builder);

    // 7) 정렬조건 추가
    Sort sort = pageable.getSort();
    sort.stream().forEach(order -> {
      Order direction = order.isAscending() ? Order.ASC : Order.DESC;
      String property = order.getProperty();
      PathBuilder orderByExpression = new PathBuilder(Schedule.class, "schedule");
      tuple.orderBy(new OrderSpecifier(direction, orderByExpression.get(property)));
    });

    // 8) tuple의 데이터를 가져오기 위한 시작 위치 지정(offset 지정)
    tuple.offset(pageable.getOffset());

    // 8) tuple의 데이터를 가져올 때 개수 지정
    tuple.limit(pageable.getPageSize());

    // 9) 최종결과를 tuple의 fetch()를 통해서 컬렉션으로 변환
    List<Schedule> result = tuple.fetch();

    // 10) tuple의 검색 결과 개수
    long count = tuple.fetchCount();
    log.info("검색 조건 - keyword: " + keyword);
    log.info("검색 결과 개수: " + count);

    // 13) Page 객체를 PageImpl 객체로 변환
    return new PageImpl<Schedule>(result, pageable, count);
  }

}
