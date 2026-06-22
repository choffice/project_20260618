package com.example.project.service;

import com.example.project.dto.PageRequestDTO;
import com.example.project.dto.PageResultDTO;
import com.example.project.dto.RegisterPlannersDTO;
import com.example.project.dto.ResponsePlannersDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.entity.InvitationStatus;
import com.example.project.entity.Notification;
import com.example.project.entity.NotificationType;
import com.example.project.entity.Planners;
import com.example.project.entity.PlannersRole;
import com.example.project.entity.PlannersUser;
import com.example.project.entity.User;
import com.example.project.entity.PlannersInvitation;
import com.example.project.repository.FavoriteRepository;
import com.example.project.repository.NotificationRepository;
import com.example.project.repository.PlannersApplicationRepository;
import com.example.project.repository.PlannersRepository;
import com.example.project.repository.PlannersUserRepository;
import com.example.project.repository.UserRepository;
import com.example.project.repository.PlannersInvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;


@Service
@Log4j2
@RequiredArgsConstructor
public class PlannersServiceImpl implements PlannersService {

  private final PlannersApplicationRepository plannersApplicationRepository;
  private final PlannersRepository plannersRepository;
  private final UserRepository userRepository;
  private final FavoriteRepository favoriteRepository;
  private final PlannersUserRepository plannersUserRepository;
  private final PlannersInvitationRepository plannersInvitationRepository;
  private final NotificationRepository notificationRepository;
  private final UserService userService;

  @Override
  public PageResultDTO<ResponsePlannersDTO, Planners> getPlannersList(PageRequestDTO pageRequestDTO, Long uid) {
    Page<Planners> page = plannersRepository.searchPage(
        pageRequestDTO.getKeyword(), pageRequestDTO.getLocation(), pageRequestDTO.getCategory(),
        pageRequestDTO.getPageable(getPlannersSort(pageRequestDTO.getSort())));

    return toPageResult(page, uid);
  }

  @Override
  public PageResultDTO<ResponsePlannersDTO, Planners> getMyPlannersList(PageRequestDTO pageRequestDTO, Long uid) {
    Page<Planners> page = plannersRepository.searchMyPlannersPage(
        pageRequestDTO.getKeyword(), pageRequestDTO.getLocation(), pageRequestDTO.getCategory(), uid,
        pageRequestDTO.getPageable(getPlannersSort(pageRequestDTO.getSort())));

    return toPageResult(page, uid);
  }

  @Override
  public PageResultDTO<ResponsePlannersDTO, Planners> getInvitedPlannersList(PageRequestDTO pageRequestDTO, Long uid) {
    Page<Planners> page = plannersRepository.searchInvitedPlannersPage(
        pageRequestDTO.getKeyword(), pageRequestDTO.getLocation(), pageRequestDTO.getCategory(), uid,
        pageRequestDTO.getPageable(getPlannersSort(pageRequestDTO.getSort())));

    return toPageResult(page, uid);
  }

  @Override
  public PageResultDTO<ResponsePlannersDTO, Planners> getCreatedPlannersList(PageRequestDTO pageRequestDTO, Long uid) {
    Page<Planners> page = plannersRepository.searchCreatedPlannersPage(
        pageRequestDTO.getKeyword(), pageRequestDTO.getLocation(), pageRequestDTO.getCategory(), uid,
        pageRequestDTO.getPageable(getPlannersSort(pageRequestDTO.getSort())));

    return toPageResult(page, uid);
  }

  @Override
  public PageResultDTO<ResponsePlannersDTO, Planners> getFavoritePlannersList(PageRequestDTO pageRequestDTO, Long uid) {
    Page<Planners> page = plannersRepository.searchFavoritePlannersPage(
        pageRequestDTO.getKeyword(), pageRequestDTO.getLocation(), pageRequestDTO.getCategory(), uid,
        pageRequestDTO.getPageable(getPlannersSort(pageRequestDTO.getSort())));

    return toPageResult(page, uid);
  }

  private PageResultDTO<ResponsePlannersDTO, Planners> toPageResult(Page<Planners> page, Long uid) {
    Function<Planners, ResponsePlannersDTO> fn = planners -> {
      ResponsePlannersDTO dto = entityToDto(planners);

      if (uid != null) {
        dto.setFavorite(favoriteRepository.existsByUidAndTid(uid, planners.getTid())); // 해당 플래너즈의 찜하기 여부 확인
      }

      return dto;
    };

    return new PageResultDTO<>(page, fn);
  }

  private Sort getPlannersSort(String sort) { // 정렬 옵션
    if ("name".equalsIgnoreCase(sort)) {
      return Sort.by(Sort.Order.asc("name"), Sort.Order.desc("tid"));
    }

    if ("popular".equalsIgnoreCase(sort)) {
      return Sort.by(Sort.Order.desc("monthlyJoinCount"), Sort.Order.desc("tid"));
    }

    return Sort.by(Sort.Order.desc("tid"));
  }

  @Override
  public Long createPlanners(RegisterPlannersDTO registerPlannersDTO, Long uid,
      MultipartFile plannersThumbnail, MultipartFile plannersBanner,
      String uploadPath) throws IOException {
    if (plannersRepository.existsByName(registerPlannersDTO.getName())) {
      throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
    }

    validateImage(plannersThumbnail);
    validateImage(plannersBanner);

    registerPlannersDTO.setPlannersThumbnail("/img/thumbnail.png");
    registerPlannersDTO.setPlannersBanner("/img/banner.png");

    if (plannersThumbnail != null && !plannersThumbnail.isEmpty()) {
      registerPlannersDTO.setPlannersThumbnail(savePlannersImage(
          plannersThumbnail, uploadPath, "thumbnail", "/plannersThumbnail/"));
    }

    if (plannersBanner != null && !plannersBanner.isEmpty()) {
      registerPlannersDTO.setPlannersBanner(savePlannersImage(
          plannersBanner, uploadPath, "banner", "/plannersBanner/"));
    }

    Planners planners = dtoToEntity(registerPlannersDTO);
    Optional<User> user = userRepository.findById(uid);
    planners.setOwner(user.get());
    PlannersUser plannersUser = PlannersUser.builder()
        .user(user.get())
        .planners(planners)
        .role(PlannersRole.ADMIN)
        .build();

    plannersRepository.save(planners);
    planners.getPlannersUsers().add(plannersUser);
    plannersUserRepository.save(plannersUser);

    return planners.getTid();
  }

  @Override
  public Long modifyPlanners(RegisterPlannersDTO registerPlannersDTO, Long uid, MultipartFile plannersThumbnail,
      MultipartFile plannersBanner, String uploadPath) throws IOException {
    Optional<Planners> result = plannersRepository.findById(registerPlannersDTO.getTid());
    if (result.isEmpty()) {
      throw new IllegalArgumentException("존재하지 않는 플래너즈입니다.");
    }

    Planners planners = result.get();

    if (planners.getPopulation() > registerPlannersDTO.getMaxPopulation()) {
      throw new IllegalArgumentException("인원 제한은 현재 인원 수보다 작을 수 없습니다.");
    }

    // 이미지 유효성 검사
    validateImage(plannersThumbnail);
    validateImage(plannersBanner);

    // 기존 이미지 경로 유지
    if (plannersThumbnail == null || plannersThumbnail.isEmpty()) {
      registerPlannersDTO.setPlannersThumbnail(planners.getPlannersThumbnail());
    } else {
      registerPlannersDTO.setPlannersThumbnail(savePlannersImage(
          plannersThumbnail, uploadPath, "thumbnail", "/plannersThumbnail/"));
    }

    if (plannersBanner == null || plannersBanner.isEmpty()) {
      registerPlannersDTO.setPlannersBanner(planners.getPlannersBanner());
    } else {
      registerPlannersDTO.setPlannersBanner(savePlannersImage(
          plannersBanner, uploadPath, "banner", "/plannersBanner/"));
    }

    // 플래너즈 정보 업데이트
    planners.changeDescription(registerPlannersDTO.getDescription());
    planners.changeStatus(registerPlannersDTO.getStatus());
    planners.changeMaxPopulation(registerPlannersDTO.getMaxPopulation());
    planners.changeThumbnail(registerPlannersDTO.getPlannersThumbnail());
    planners.changeBanner(registerPlannersDTO.getPlannersBanner());
    plannersRepository.save(planners);

    return planners.getTid();
  }

  @Override
  public void deletePlanners(Long tid) {
    plannersRepository.deleteById(tid);
  }

  private void validateImage(MultipartFile image) {
    if (image != null && !image.isEmpty() && !image.getContentType().startsWith("image")) {
      throw new IllegalArgumentException("이미지 형식이 올바르지 않습니다.");
    }
  }

  private String savePlannersImage(MultipartFile image, String uploadPath, String imageType, String webPrefix)
      throws IOException {
    String originalName = image.getOriginalFilename();
    String fileName = originalName == null ? "image" : originalName.substring(originalName.lastIndexOf("\\") + 1);
    String folderPath = makeFolder(uploadPath, imageType);
    String uuid = UUID.randomUUID().toString();
    String saveFileName = uuid + "_" + fileName;

    String saveName = uploadPath + File.separator + "planners" + File.separator + imageType
        + File.separator + folderPath + File.separator + saveFileName;
    Path savePath = Paths.get(saveName);
    image.transferTo(savePath);

    return webPrefix + folderPath.replace(File.separator, "/") + "/" + saveFileName;
  }

  private String makeFolder(String uploadPath, String imageType) {
    String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    String folderPath = str.replace("/", File.separator);
    File uploadPathFolder = new File(uploadPath + File.separator + "planners" + File.separator + imageType, folderPath);

    if (!uploadPathFolder.exists()) {
      uploadPathFolder.mkdirs();
    }

    return folderPath;
  }

  @Override
  public List<ResponsePlannersDTO> getPopularPlanners(Long uid) {
    List<Planners> result = plannersRepository.findPopularPlanners();
    List<ResponsePlannersDTO> dto = result.stream().map(this::entityToDto).toList();
    dto.forEach(responsePlannersDTO -> {
      responsePlannersDTO.setFavorite(favoriteRepository.existsByUidAndTid(uid, responsePlannersDTO.getTid()));
    });
    return dto;
  }

  @Override
  public List<ResponsePlannersDTO> getRecommendedPlanners(Long uid) {
    List<Planners> result = plannersRepository.findRecommendedPlanners(uid);
    List<ResponsePlannersDTO> dto = result.stream().map(this::entityToDto).toList();
    dto.forEach(responsePlannersDTO -> {
      responsePlannersDTO.setFavorite(favoriteRepository.existsByUidAndTid(uid, responsePlannersDTO.getTid()));
    });
    System.out.println(uid);
    System.out.println(dto);
    return dto;
  }

  @Override
  @Transactional(readOnly = true)
  public ResponsePlannersDTO getPlannersById(Long tid) {
    Optional<Planners> result = plannersRepository.findById(tid);
    if (result.isPresent())
      return entityToDto(result.get());

    return null;
  }
  
  @Override
  public ResponseUserDTO getOwner(Long tid) {
    PlannersUser result = plannersUserRepository.getOwner(tid);
    ResponseUserDTO user = userService.entityToDto(result.getUser());
    
    return user; 
  }

  @Override
  public int getMemberCount(Long tid) {
    return plannersUserRepository.getMemberList(tid).size();
  }

  @Override
  public List<ResponseUserDTO> getMemberList(Long tid) {
    List<PlannersUser> result = plannersUserRepository.getMemberList(tid);
    List<User> user = result.stream().map(r -> r.getUser()).toList();
    
    return user.stream().map(userService::entityToDto).toList();
  }

  public List<ResponseUserDTO> getPreviewMembers(Long tid) {
    List<PlannersUser> members = plannersUserRepository.getPreviewMembers(tid, PageRequest.of(0, 6));
    List<User> result = members.stream().map(PlannersUser::getUser).toList();

    return result.stream().map(userService::entityToDto).toList();
  }

  public Map<String, Object> getPlannersUserByTid(Long tid, int memberPage) {
    Optional<Planners> planners = plannersRepository.findById(tid);
    ResponseUserDTO ownerDTO = null;
    if (planners.isPresent()) {
      // 방장 추출
      User owner = planners.get().getOwner();
      ownerDTO = userService.entityToDto(owner);
    }
    Map<String, Object> members = new HashMap<>();
    Pageable pageable = PageRequest.of(memberPage - 1, 10);
    Page<PlannersUser> page = plannersUserRepository.findByPlanners_Tid(tid, pageable);
    Page<ResponseUserDTO> result = page.map(PlannersUser::getUser).map(userService::entityToDto);
    members.put("owner", ownerDTO);
    members.put("members", result);

    return members;
  }
  
  //오창 20260618 플래너 삭제
  @Override
  @Transactional
  public Long deleteUserFromPlanners(Long tid, Long uid){
    boolean result = plannersUserRepository.existsByPlanners_TidAndUser_Uid(tid, uid);
    if(result) {
      plannersUserRepository.deletePlannersUserByPlanners_TidAndUser_Uid(tid, uid);
      plannersApplicationRepository.deleteByPlanners_TidAndUser_Uid(tid, uid);
      plannersRepository.findById(tid).get().decreasePopulation();
      return uid;
    }

    return 0L;
  }

  // public Map<String, List<Object>> getPlannersUserByTid(Long tid) {
  // List<PlannersUser> result = plannersUserRepository.findByPlanners_Tid(tid);
  // // result.get(0).getMid();
  // User o = result.get(0).getPlanners().getOwner();
  // Map<String, List<Object>> members = new HashMap<>();
  // //방장 추출
  // members.put("owner", Arrays.asList(o.getUid(), o.getName(),
  // o.getProfileImg()));
  // //멤버 추출
  // List<Object> notowner = new ArrayList<>();
  // result.forEach(m -> {
  // if (m.getRole().equals(PlannersRole.USER)) {
  // Object[] arr = {m.getUser().getUid(), m.getUser().getName(),
  // m.getUser().getProfileImg()};
  // notowner.add(arr);
  // }
  // });
  // members.put("members", notowner);

  // if (!result.isEmpty()) return members;
  // return null;
  // }

  @Override
  public Long inviteUser(Long uid, Long tid, String email) {
    Optional<Planners> planners = plannersRepository.findById(tid);
    Optional<User> inviter = userRepository.findById(uid);
    Optional<User> invitee = userRepository.findByEmail(email);

    if (planners.isEmpty() || inviter.isEmpty() || invitee.isEmpty()) {
      return -2L; // 값이 존재하지 않음
    }

    if (plannersUserRepository.existsByPlanners_TidAndUser_Uid(tid, invitee.get().getUid())) {
      return -1L; // 이미 플래너즈에 가입되어 있음
    }

    if (plannersInvitationRepository.existsByPlanners_TidAndInvitee_Uid(tid, invitee.get().getUid())) {
      return 0L; // 이미 초대를 보낸 상태임
    }

    PlannersInvitation invitation = PlannersInvitation.builder()
        .planners(planners.get())
        .inviter(inviter.get())
        .invitee(invitee.get())
        .status(InvitationStatus.PENDING)
        .build();
    plannersInvitationRepository.save(invitation);

    Notification notification = Notification.builder()
        .user(invitee.get())
        .title(planners.get().getName() + " 플래너즈로부터 초대 받았습니다!")
        .url("/planners/invited/list")
        .type(NotificationType.ACTIVITY)
        .build();
    notificationRepository.save(notification);
    return 1L;
  }

  @Override
  @Transactional
  public Long acceptInvitation(Long uid, Long tid) {
    Optional<PlannersInvitation> temp = plannersInvitationRepository.findByPlanners_TidAndInvitee_UidAndStatus(tid, uid,
        InvitationStatus.PENDING);
    if (temp.isPresent()) {
      PlannersInvitation invitation = temp.get();
      Planners planners = invitation.getPlanners();
      User invitee = invitation.getInvitee();

      if (!plannersUserRepository.existsByPlanners_TidAndUser_Uid(tid, uid)) {
        if (planners.getPopulation() >= planners.getMaxPopulation()) {
          return 0L; // 정원 초과
        }

        PlannersUser plannersUser = PlannersUser.builder()
            .planners(planners)
            .user(invitee)
            .role(PlannersRole.USER)
            .build();
        plannersUserRepository.save(plannersUser);
        planners.increasePopulation();
        plannersRepository.save(planners);
      }

      plannersInvitationRepository.delete(invitation);
      return 1L;
    }
    return -1L;
  }

  @Override
  @Transactional
  public Long declineInvitation(Long uid, Long tid) {
    Optional<PlannersInvitation> temp = plannersInvitationRepository.findByPlanners_TidAndInvitee_UidAndStatus(tid, uid,
        InvitationStatus.PENDING);
    if (temp.isPresent()) {
      PlannersInvitation invitation = temp.get();
      plannersInvitationRepository.delete(invitation);
      return 1L;
    }
    return 0L;
  }

  @Override
  public List<ResponsePlannersDTO> getMyPlanners(Long uid) {
    List<Planners> result = plannersRepository.findMyPlanners(uid);
    List<ResponsePlannersDTO> dto = result.stream().map(this::entityToDto).toList();
    dto.forEach(responsePlannersDTO -> {
      responsePlannersDTO.setFavorite(favoriteRepository.existsByUidAndTid(uid, responsePlannersDTO.getTid()));
    });
    return dto;
  }

  @Override
  public boolean isMember(Long tid, Long uid) {
    return plannersUserRepository.existsByPlanners_TidAndUser_Uid(tid, uid);
  }
}
