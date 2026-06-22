package com.example.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import com.example.project.entity.User;

import com.example.project.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
  // 이름으로 친구 검색
  @Query("select f from Friend f where f.status = 'ACCEPTED' and " +
         "((f.requester.uid = :uid and lower(f.receiver.name) like lower(concat('%', :keyword, '%'))) or " +
         "(f.receiver.uid = :uid and lower(f.requester.name) like lower(concat('%', :keyword, '%'))))")
  List<Friend> findByNameContaining(@Param("uid") Long uid, @Param("keyword") String keyword);

  // 유저 아이디로 친구 목록 조회
  @Query("select f from Friend f where f.status = 'ACCEPTED' and (f.requester.uid = :uid or f.receiver.uid = :uid)")
  List<Friend> findFriends(@Param("uid") Long uid);

  @Query("select f from Friend f where f.status = 'BLOCKED' and " +
    "((f.requester.uid = :uid and lower(f.receiver.name) like lower(concat('%', :keyword, '%'))) or " +
    "(f.receiver.uid = :uid and lower(f.requester.name) like lower(concat('%', :keyword, '%'))))")
  List<Friend> findBlockedByNameContaining(@Param("uid") Long uid, @Param("keyword") String keyword);

  @Query("select f from Friend f where f.status = 'PENDING' and f.receiver.uid = :uid and lower(f.requester.name) like lower(concat('%', :keyword, '%'))")
  List<Friend> findFriendRequestsByNameContaining(@Param("uid") Long uid, @Param("keyword") String keyword);

  // 친구 요청 조회
  @Query("select f from Friend f where f.status = 'PENDING' and (f.requester.uid = :requesterUid and f.receiver.uid = :receiverUid)")
  Friend findFriend(@Param("requesterUid") Long requesterUid, @Param("receiverUid") Long receiverUid);

  // 친구 요청을 이미 보냈는지 여부 확인
  @Query("select count(f) > 0 from Friend f where (f.requester.uid = :requesterUid and f.receiver.uid = :receiverUid) or (f.requester.uid = :receiverUid and f.receiver.uid = :requesterUid)")
  boolean existsByRequesterAndReceiver(@Param("requesterUid") Long requesterUid, @Param("receiverUid") Long receiverUid);

  // 친구 요청 목록 조회
  @Query("select f from Friend f where f.status = 'PENDING' and f.receiver.uid = :uid")
  List<Friend> findFriendRequests(@Param("uid") Long uid);

  // 차단한 사용자 목록 조회
  @Query("select f from Friend f where f.status = 'BLOCKED' and (f.requester.uid = :uid or f.receiver.uid = :uid)")
  List<Friend> findBlocked(@Param("uid") Long uid);
}
