package com.example.project.repository.search;

import com.example.project.entity.InvitationStatus;
import com.example.project.entity.Planners;
import com.example.project.entity.PlannersStatus;
import com.example.project.entity.QFavorite;
import com.example.project.entity.QPlanners;
import com.example.project.entity.QPlannersInvitation;
import com.example.project.entity.QPlannersUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
public class SearchPlannersRepositoryImpl extends QuerydslRepositorySupport implements SearchPlannersRepository {

  private enum PlannersSearchScope {
    PUBLIC, JOINED, INVITED, CREATED, FAVORITE
  }

  public SearchPlannersRepositoryImpl() {
    super(Planners.class);
  }

  @Override
  public Page<Planners> searchPage(String keyword, String location, String category, Pageable pageable) {
    return searchPage(keyword, location, category, null, pageable, PlannersSearchScope.PUBLIC);
  }

  @Override
  public Page<Planners> searchMyPlannersPage(String keyword, String location, String category, Long uid, Pageable pageable) {
    return searchPage(keyword, location, category, uid, pageable, PlannersSearchScope.JOINED);
  }

  @Override
  public Page<Planners> searchInvitedPlannersPage(String keyword, String location, String category, Long uid, Pageable pageable) {
    return searchPage(keyword, location, category, uid, pageable, PlannersSearchScope.INVITED);
  }

  @Override
  public Page<Planners> searchCreatedPlannersPage(String keyword, String location, String category, Long uid, Pageable pageable) {
    return searchPage(keyword, location, category, uid, pageable, PlannersSearchScope.CREATED);
  }

  @Override
  public Page<Planners> searchFavoritePlannersPage(String keyword, String location, String category, Long uid, Pageable pageable) {
    return searchPage(keyword, location, category, uid, pageable, PlannersSearchScope.FAVORITE);
  }

  private Page<Planners> searchPage(String keyword, String location, String category, Long uid,
                                    Pageable pageable, PlannersSearchScope scope) {
    QPlanners qPlanners = QPlanners.planners;
    QPlannersUser qPlannersUser = QPlannersUser.plannersUser;
    QPlannersInvitation qPlannersInvitation = QPlannersInvitation.plannersInvitation;
    QFavorite qFavorite = QFavorite.favorite;

    JPQLQuery<Planners> jpqlQuery = from(qPlanners);
    JPQLQuery<Planners> tuple = jpqlQuery.select(qPlanners);

    BooleanBuilder builder = new BooleanBuilder();
    BooleanExpression expression = qPlanners.tid.gt(0L);
    builder.and(expression);
    applyScope(builder, scope, uid, qPlanners, qPlannersUser, qPlannersInvitation, qFavorite);

    if (keyword != null && !keyword.isEmpty()) {
      BooleanBuilder keywordCondition = new BooleanBuilder();
      keywordCondition.or(qPlanners.name.containsIgnoreCase(keyword));
      keywordCondition.or(qPlanners.description.containsIgnoreCase(keyword));
      builder.and(keywordCondition);
    }

    if (location != null && !location.isEmpty()) {
      builder.and(qPlanners.location.containsIgnoreCase(location));
    }

    if (category != null && !category.isEmpty()) {
      builder.and(qPlanners.category.containsIgnoreCase(category));
    }

    tuple.where(builder);
    applySort(tuple, pageable.getSort(), qPlannersUser, qPlanners);

    long count = tuple.fetchCount();
    tuple.offset(pageable.getOffset());
    tuple.limit(pageable.getPageSize());
    List<Planners> result = tuple.fetch();

    log.info("planners search scope: " + scope + ", keyword: " + keyword
        + ", location: " + location + ", category: " + category + ", count: " + count);

    return new PageImpl<>(result, pageable, count);
  }

  private void applyScope(BooleanBuilder builder, PlannersSearchScope scope, Long uid,
                          QPlanners qPlanners, QPlannersUser qPlannersUser,
                          QPlannersInvitation qPlannersInvitation, QFavorite qFavorite) {
    if (scope == PlannersSearchScope.PUBLIC) {
      builder.and(qPlanners.status.eq(PlannersStatus.PUBLIC));
      return;
    }

    if (uid == null) {
      builder.and(qPlanners.tid.lt(0L));
      return;
    }

    if (scope == PlannersSearchScope.JOINED) {
      builder.and(JPAExpressions.selectOne()
          .from(qPlannersUser)
          .where(qPlannersUser.planners.eq(qPlanners)
              .and(qPlannersUser.user.uid.eq(uid)))
          .exists());
      return;
    }

    if (scope == PlannersSearchScope.INVITED) {
      builder.and(JPAExpressions.selectOne()
          .from(qPlannersInvitation)
          .where(qPlannersInvitation.planners.eq(qPlanners)
              .and(qPlannersInvitation.invitee.uid.eq(uid))
              .and(qPlannersInvitation.status.eq(InvitationStatus.PENDING)))
          .exists());
      return;
    }

    if (scope == PlannersSearchScope.FAVORITE) {
      builder.and(JPAExpressions.selectOne()
          .from(qFavorite)
          .where(qFavorite.user.uid.eq(uid)
              .and(qFavorite.planners.eq(qPlanners)))
          .exists());
      return;
    }

    builder.and(qPlanners.owner.uid.eq(uid));
  }

  private void applySort(JPQLQuery<Planners> tuple, Sort sort, QPlannersUser qPlannersUser, QPlanners qPlanners) {
    sort.stream().forEach(order -> {
      Order direction = order.isAscending() ? Order.ASC : Order.DESC;
      String property = order.getProperty();

      if ("monthlyJoinCount".equals(property)) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        tuple.orderBy(new OrderSpecifier<>(direction,
            JPAExpressions.select(qPlannersUser.count())
                .from(qPlannersUser)
                .where(qPlannersUser.planners.eq(qPlanners)
                    .and(qPlannersUser.regDate.goe(oneMonthAgo)))));
        return;
      }

      PathBuilder orderByExpression = new PathBuilder(Planners.class, "planners");
      tuple.orderBy(new OrderSpecifier(direction, orderByExpression.get(property)));
    });
  }
}
