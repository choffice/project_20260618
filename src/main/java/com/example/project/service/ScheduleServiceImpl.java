package com.example.project.service;

import com.example.project.dto.PageRequestDTO;
import com.example.project.dto.PageResultDTO;
import com.example.project.dto.RegisterScheduleDTO;
import com.example.project.dto.ResponseScheduleDTO;
import com.example.project.dto.ScheduleBlockDTO;
import com.example.project.dto.ScheduleParticipantDTO;
import com.example.project.entity.*;
import com.example.project.repository.ActivityBlockRepository;
import com.example.project.repository.PlannersRepository;
import com.example.project.repository.ScheduleBlockRepository;
import com.example.project.repository.ScheduleParticipantRepository;
import com.example.project.repository.ScheduleRepository;
import com.example.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.PageImpl;

@Service
@Log4j2
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
  private final PlannersServiceImpl plannersServiceImpl;
  private final PlannersRepository plannersRepository;
  private final ScheduleParticipantRepository scheduleParticipantRepository;
  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;
  private final ActivityBlockRepository activityBlockRepository;
  private final ScheduleBlockRepository scheduleBlockRepository;

  @Override
  public ResponseScheduleDTO get(Long sid, Long uid) {
    Optional<Schedule> result = scheduleRepository.findById(sid);
    if (result.isPresent()) {
      ResponseScheduleDTO dto = entityToDto(result.get());

      if (uid != null) {
        if (isCreator(sid, uid)) {
          dto.setUserStatus(ScheduleRole.CREATOR);
        } else if (isJoiner(sid, uid)) {
          dto.setUserStatus(ScheduleRole.FOLLOWER);
        } else {
          dto.setUserStatus(ScheduleRole.NONE);
        }
      } else {
        // uid가 null이면 로그인하지 않은 상태 → 기본값 유지
        dto.setUserStatus(ScheduleRole.NONE);
      }

      System.out.println(">>"+dto);
      return dto;
    }
    return null;
  }

  // @Override
  // public PageResultDTO<ResponseScheduleDTO, Schedule> getScheduleList(Long tid, Long uid, PageRequestDTO pageRequestDTO) {
  //   // 동적 검색이 없을 경우
  //   Page<Schedule> result = scheduleRepository.findByPlanners_Tid(
  //       tid,
  //       pageRequestDTO.getPageable(Sort.by("sid").descending())
  //   );

  //   // 동적 검색이 있는 경우
  //   // Page<Schedule> page = scheduleRepository.searchPage(
  //   //     pageRequestDTO.getKeyword(),
  //   //     pageRequestDTO.getPageable(Sort.by("sid").descending())
  //   // );

  //   // ScheduleService에 default 정의
  //   Function<Schedule, ResponseScheduleDTO> fn = (schedule) -> {
  //     ResponseScheduleDTO dto = entityToDto(schedule);

  //     if (uid != null) {
  //       if (isCreator(schedule.getSid(), uid)) {
  //         dto.setUserStatus(ScheduleRole.CREATOR);
  //       } else if (isJoiner(schedule.getSid(), uid)) {
  //         dto.setUserStatus(ScheduleRole.FOLLOWER);
  //       } else {
  //         dto.setUserStatus(ScheduleRole.NONE);
  //       }
  //     } else {
  //       dto.setUserStatus(ScheduleRole.NONE); // 로그인하지 않은 경우
  //     }

  //     return dto;
  //   };
  // }

  @Override
  public PageResultDTO<ResponseScheduleDTO, Schedule> getScheduleList(Long tid, Long uid, PageRequestDTO pageRequestDTO, String filterStatus) {

    // 기본 조회
    Page<Schedule> result = scheduleRepository.findByPlanners_Tid(
        tid,
        pageRequestDTO.getPageable(Sort.by("sid").descending())
    );

    // 필터링 로직
    Page<Schedule> filteredPage;
    if (filterStatus != null) {
        List<Schedule> filtered = result.getContent().stream()
            .filter(s -> s.getScheduleStatus().toString().equals(filterStatus))
            .toList();

        filteredPage = new PageImpl<>(filtered, result.getPageable(), filtered.size());
    } else {
        // 필터링 없이 전체 반환
        filteredPage = result;
    }

    // 변환 함수 fn 그대로 적용
    Function<Schedule, ResponseScheduleDTO> fn = (schedule) -> {
        ResponseScheduleDTO dto = entityToDto(schedule);

        if (uid != null) {
            if (isCreator(schedule.getSid(), uid)) {
                dto.setUserStatus(ScheduleRole.CREATOR);
            } else if (isJoiner(schedule.getSid(), uid)) {
                dto.setUserStatus(ScheduleRole.FOLLOWER);
            } else {
                dto.setUserStatus(ScheduleRole.NONE);
            }
        } else {
            dto.setUserStatus(ScheduleRole.NONE); // 로그인하지 않은 경우
        }

        return dto;
    };

    return new PageResultDTO<>(filteredPage, fn);
  }


  // 관언시 잘 쓰겠습니다
  @Override
  public Long createSchedule(@RequestParam Long tid, Long uid, RegisterScheduleDTO registerScheduleDTO, MultipartFile scheduleThumbnail, String uploadPath) throws IOException {
    if (scheduleRepository.existsByTitle(registerScheduleDTO.getTitle())) {
    throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
    }

    validateImage(scheduleThumbnail);
    
    registerScheduleDTO.setScheduleThumbnail("/img/thumbnail.png");
    
    if (scheduleThumbnail != null && !scheduleThumbnail.isEmpty()) {
    registerScheduleDTO.setScheduleThumbnail(saveScheduleImage(
    scheduleThumbnail, uploadPath, "thumbnail", "/scheduleThumbnail/"));
    }

    Schedule schedule = dtoToEntity(registerScheduleDTO);
    Optional<User> user = userRepository.findById(uid);
    Optional<Planners> planners = plannersRepository.findById(tid);
    schedule.setPlanners(planners.get());
    schedule.setCreator(user.get());
    schedule.setScheduleStatus(ScheduleStatus.PENDING);

    scheduleRepository.save(schedule);
    if (schedule.getSid() != null) {
      ScheduleParticipant scheduleParticipant = ScheduleParticipant.builder()
          .user(user.get())
          .schedule(schedule)
          .build();
      scheduleParticipantRepository.save(scheduleParticipant);
    }

    return schedule.getSid();
  }

  
  private void validateImage(MultipartFile image) {
    if (image != null && !image.isEmpty() && !image.getContentType().startsWith("image")) {
      throw new IllegalArgumentException("이미지 형식이 올바르지 않습니다.");
    }
  }

  private String saveScheduleImage(MultipartFile image, String uploadPath, String imageType, String webPrefix)
      throws IOException {
    String originalName = image.getOriginalFilename();
    String fileName = originalName == null ? "image" : originalName.substring(originalName.lastIndexOf("\\") + 1);
    String folderPath = makeFolder(uploadPath, imageType);
    String uuid = UUID.randomUUID().toString();
    String saveFileName = uuid + "_" + fileName;

    String saveName = uploadPath + File.separator + "schedule" + File.separator + imageType
        + File.separator + folderPath + File.separator + saveFileName;
    Path savePath = Paths.get(saveName);
    image.transferTo(savePath);

    return webPrefix + folderPath.replace(File.separator, "/") + "/" + saveFileName;
  }

  private String makeFolder(String uploadPath, String imageType) {
    String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    String folderPath = str.replace("/", File.separator);
    File uploadPathFolder = new File(uploadPath + File.separator + "schedule" + File.separator + imageType, folderPath);

    if (!uploadPathFolder.exists()) {
      uploadPathFolder.mkdirs();
    }

    return folderPath;
  }


  @Override
  public boolean isJoiner(Long sid, Long uid){

    // return existsBySchedule_SidAndUser_Uid(Long sid, Long uid);
    return scheduleParticipantRepository.existsBySchedule_SidAndUser_Uid(sid,uid);
  }

  // 일정을 만든 사람 or 플래너즈 관리자인지 여부 조회 (True or False)
  @Override
  public boolean isCreator(Long sid, Long uid){
    Optional<Schedule> schedule = scheduleRepository.findById(sid);
    if (schedule.isPresent()) {
      if (uid == plannersServiceImpl.getOwner(schedule.get().getPlanners().getTid()).getUid() || 
          scheduleRepository.existsBySidAndCreator_Uid(sid,uid)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Long submitSchedule(Long sid, List<ScheduleBlockDTO> scheduleBlockDTO) {
    if (scheduleBlockDTO.isEmpty()) {
      return -1L;
    }
    for (ScheduleBlockDTO dto : scheduleBlockDTO) {
      Optional<ActivityBlock> activityBlock = activityBlockRepository.findById(dto.getBid());
      Optional<Schedule> schedule = scheduleRepository.findById(sid);

      if (activityBlock.isPresent() && schedule.isPresent()) {
        ScheduleBlock scheduleBlock = ScheduleBlock.builder()
            .block(activityBlockRepository.findById(dto.getBid()).get())
            .schedule(scheduleRepository.findById(sid).get())
            .startTime(LocalDateTime.of(dto.getDate(), dto.getStartTime()))
            .endTime(LocalDateTime.of(dto.getEndTime().isBefore(dto.getStartTime()) ? dto.getDate().plusDays(1) : dto.getDate(), dto.getEndTime()))
            .build();
        scheduleBlockRepository.save(scheduleBlock);
        schedule.get().setScheduleStatus(ScheduleStatus.SCHEDULED);
        scheduleRepository.save(schedule.get());
      } else {
        return 0L;
      }
    }
    return 1L;
  }

  @Override
  public Long removeSchedule(Long sid, Long uid) {
    Optional<Schedule> schedule = scheduleRepository.findById(sid);

    if (schedule.isPresent()) {
      if (isCreator(sid, uid)) {  // 일정 생성자가 맞거나 플래너즈 관리자일시
        scheduleRepository.delete(schedule.get());
        return 1L;
      }
      return 0L;
    }
    return -1L;
  }
  
  //스케쥴 블록 땡겨오기
  @Override
  public List<ScheduleBlockDTO> getScheduleBlockList(Long sid) {
    List<ScheduleBlock> blocks = scheduleBlockRepository.findBySchedule_Sid(sid);
    List<ScheduleBlockDTO> dto = blocks.stream().map(block -> 
      ScheduleBlockDTO.builder()
          .bid(block.getBid())
          .name(block.getBlock().getName())
          .dateStr(block.getStartTime().toLocalDate().toString())
          .startTimeStr(block.getStartTime().toLocalTime().toString())
          .endTimeStr(block.getEndTime().toLocalTime().toString())
          .build()
      ).toList();
  
    return dto;
  }

  //스케쥴 참가자 땡겨오기
  @Override
  public List<ScheduleParticipantDTO> getScheduleParticipantsList(Long sid) {
    List<ScheduleParticipant> people = scheduleParticipantRepository.findBySchedule_Sid(sid);
    List<ScheduleParticipantDTO> dto = people.stream().map(person -> 
      ScheduleParticipantDTO.builder()
          .pid(person.getPid())
          .uid(person.getUser().getUid())
          .sid(person.getSchedule().getSid())
          .nickname(person.getUser().getName())
          .profileImage(person.getUser().getProfileImg())
          .build()
      ).toList();
  
    return dto;
  }
}
