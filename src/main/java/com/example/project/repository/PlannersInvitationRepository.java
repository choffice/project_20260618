package com.example.project.repository;

import com.example.project.entity.InvitationStatus;
import com.example.project.entity.PlannersInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlannersInvitationRepository extends JpaRepository<PlannersInvitation, Long> {
  // 초대받은 사용자가 이미 초대를 받은 상태인지 확인
  boolean existsByPlanners_TidAndInvitee_Uid(Long tid, Long inviteeUid);

  // 초대 상태 여부에 따른 초대 조회
  Optional<PlannersInvitation> findByPlanners_TidAndInvitee_UidAndStatus(Long tid, Long inviteeUid, InvitationStatus status);
}
