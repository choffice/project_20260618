package com.example.project.service;

import com.example.project.dto.ApplicationDTO;
import com.example.project.entity.*;
import com.example.project.repository.PlannersApplicationRepository;
import com.example.project.repository.PlannersRepository;
import com.example.project.repository.PlannersUserRepository;
import com.example.project.repository.UserRepository;
import com.example.project.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

  private final PlannersRepository plannersRepository;
  private final PlannersApplicationRepository applicationRepository;
  private final PlannersUserRepository plannersUserRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional(readOnly = true)
  public String getForm(Long tid) {
    Planners planners = plannersRepository.findById(tid).orElseThrow();
    String jsonSchema = planners.getFormSchema();
    if (jsonSchema == null || jsonSchema.isEmpty()) {
      return "[]";
    }
    return jsonSchema;
  }

  @Override
  public void saveForm(Long tid, ApplicationDTO.FormSaveRequest request) throws Exception {
    Planners planners = plannersRepository.findById(tid).orElseThrow();

    // DTO 안의 객체를 JSON 문자열로 변환 (ObjectMapper 사용)
    String jsonSchema = objectMapper.writeValueAsString(request.getFormSchema());

    planners.changeFormSchema(jsonSchema);
    plannersRepository.save(planners);
  }

  @Override
  public Long submitApplication(Long tid, ApplicationDTO.SubmitRequest request, Long uid) throws Exception {
    // 사용자가 보낸 답변(객체)을 JSON 문자열로 변환
    String jsonAnswers = objectMapper.writeValueAsString(request.getAnswers());

    Planners planners = plannersRepository.findById(tid).orElseThrow();
    User user = userRepository.findById(uid).orElseThrow();

    if (applicationRepository.isSubmitted(tid, uid)) {
      return 0L;
    }

    // 프론트에서 받은 정보(DTO)를 바탕으로 Entity를 직접 조립하는 부분
    PlannersApplication application = new PlannersApplication();
    application.setPlanners(planners);
    application.setUser(user);
    application.setAnswersJson(jsonAnswers);
    application.setStatus(ApplicationStatus.PENDING);

    applicationRepository.save(application);

    // 가입 신청 알림
    User targetUser = planners.getOwner();
    Notification notification = Notification.builder()
        .user(targetUser)
        .type(NotificationType.GROUP)
        .title(user.getName() + " 님이 " + planners.getName() + " 에 들어오고 싶대요.")
        .url("/planners/admin?tid=" + planners.getTid() + "&tab=manage-applications")
        .build();
    notificationRepository.save(notification);

    return 1L;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ApplicationDTO.ApplicationResponse> getApplications(Long tid) {
    List<PlannersApplication> applications = applicationRepository.findByPlanners_Tid(tid);

    // Entity 리스트를 DTO 리스트로 변환
    return applications.stream()
        .map(this::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void approveApplication(Long applicationId) {
    PlannersApplication application = applicationRepository.findById(applicationId).orElseThrow();
    application.setStatus(ApplicationStatus.APPROVED);
    applicationRepository.save(application);
    Planners planners = application.getPlanners();
    User user = application.getUser();
    boolean exists = plannersUserRepository.existsByPlanners_TidAndUser_Uid(planners.getTid(), user.getUid());
    if (!exists) {
      PlannersUser plannersUser = PlannersUser.builder()
          .planners(planners)
          .user(user)
          .role(PlannersRole.USER) // 권한은 프로젝트 설정에 맞게 변경하세요.
          .build();
      plannersUserRepository.save(plannersUser);
      planners.increasePopulation();
      plannersRepository.save(planners);
    }

    // 승인 알림
    User targetUser = application.getUser();
    Notification notification = Notification.builder()
        .user(targetUser)
        .type(NotificationType.GROUP)
        .title(planners.getName() + "에 어서와")
        .url("/planners?tid=" + planners.getTid())
        .build();
    notificationRepository.save(notification);
  }

  @Override
  public void rejectApplication(Long applicationId) {
    PlannersApplication application = applicationRepository.findById(applicationId).orElseThrow();
    applicationRepository.delete(application);

    // 거절 알림
    User targetUser = application.getUser();
    Planners planners = application.getPlanners();
    Notification notification = Notification.builder()
        .user(targetUser)
        .type(NotificationType.GROUP)
        .title(planners.getName() + "의 단호한 거절의사표시")
        .url("/planners?tid=" + planners.getTid())
        .build();
    notificationRepository.save(notification);
  }



  private ApplicationDTO.ApplicationResponse entityToDto(PlannersApplication entity) {
    ApplicationDTO.ApplicationResponse dto =  new ApplicationDTO.ApplicationResponse();
    dto.setId(entity.getId());
    dto.setUserName(entity.getUser().getName());
    dto.setUserEmail(entity.getUser().getEmail());
    dto.setStatus(entity.getStatus().name());

    try {
      // DB에 문자로 저장되어 있던 JSON 답변을 다시 List<Map> 객체 형태로 되돌려 포장합니다.
      List<Map<String, String>> answers = objectMapper.readValue(
          entity.getAnswersJson(),
          new TypeReference<List<Map<String, String>>>() {}
      );
      dto.setAnswers(answers);
    } catch (Exception e) {
      dto.setAnswers(List.of());
    }

    return dto;
  }
}
