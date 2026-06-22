package com.example.project.controller;

import com.example.project.dto.*;
import com.example.project.entity.Schedule;
import com.example.project.entity.ScheduleStatus;
import com.example.project.security.dto.AuthUserDTO;
import com.example.project.service.FavoriteService;
import com.example.project.service.PlannersService;
import com.example.project.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/planners/schedule")
public class ScheduleController {

  private final String prefix = "/planners/schedule/";

  @Value("${com.example.upload.path}")
  private String uploadPath;

  private final PlannersService plannersService;
  private final ScheduleService scheduleService;
  private final FavoriteService favoriteService;

  private void getPlannersDTO(Long tid, Model model, @AuthenticationPrincipal AuthUserDTO user) {
    // 플래너즈 정보
    ResponsePlannersDTO responsePlannersDTO = plannersService.getPlannersById(tid);
    // 로그인 유저 식별
    Long uid = user != null ? user.getUid() : null;
    if (responsePlannersDTO != null) {
      model.addAttribute("plannersModifyThumbnail", responsePlannersDTO.getPlannersThumbnail());
      model.addAttribute("plannersModifyBanner", responsePlannersDTO.getPlannersBanner());

      if (responsePlannersDTO.getPlannersBanner().equals("/img/banner.png"))
        responsePlannersDTO.setPlannersBanner(
            "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1800&q=80");
      if (responsePlannersDTO.getPlannersThumbnail().equals("/img/thumbnail.png"))
        responsePlannersDTO.setPlannersThumbnail("/img/empty_thumbnail.png");
      model.addAttribute("plannersDTO", responsePlannersDTO);
      model.addAttribute("currentUserUid", uid);
      model.addAttribute("owner", plannersService.getOwner(tid));
      model.addAttribute("isMember", plannersService.isMember(tid, uid));
      model.addAttribute("tid", tid);
      // model.addAttribute("memberList",plannersService.getPlannersUserByTid(tid));
      // 식별한 유저를 통해 찜하기 여부 제출
      if (uid != null && favoriteService.get(uid).contains(tid))
        model.addAttribute("active", "active");
    }
  }

  @GetMapping({"", "/", "board"})
  public String list(@AuthenticationPrincipal AuthUserDTO user, @RequestParam(value = "status", required = false) String status, 
                     @RequestParam("tid") Long tid, Model model, PageRequestDTO pageRequestDTO) {
                    
    Long uid = (user != null) ? user.getUid() : null; // null-safe 처리
    PageResultDTO<ResponseScheduleDTO, Schedule> result = scheduleService.getScheduleList(tid, uid, pageRequestDTO, status);

    getPlannersDTO(tid, model, user);

    model.addAttribute("scheduleList", result);

    return prefix + "board";
  }

  @GetMapping("/view")
  public ResponseEntity<Map<String, Object>> getSchedule(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid, @RequestParam("sid") Long sid) {
    // uid가 null일 수 있으니 안전하게 처리
    Long uid = (user != null) ? user.getUid() : null;
    Map<String, Object> body = new HashMap<>();
    ResponseScheduleDTO result = scheduleService.get(sid, uid);
    if (result != null) {
      System.out.println(">>>"+result.getScheduleThumbnail());
     body.put("schDTO", result);
     body.put("isMember", plannersService.isMember(tid, uid));
      return ResponseEntity.ok(body);
    }
    return ResponseEntity.notFound().build();
  }

  // 일정 등록 작성
  @GetMapping("invite")
  public String createPlan(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid, Model model,
                           PageRequestDTO pageRequestDTO) {
    // 플래너즈 DTO
    getPlannersDTO(tid, model, user);
    return prefix + "invite";
  }

  @PostMapping("invite")
  public String submitPlan(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid, Model model,
                           @RequestParam(value = "scheduleThumbnailFile", required = false) MultipartFile scheduleThumbnail,
                           RegisterScheduleDTO registerScheduleDTO, PageRequestDTO pageRequestDTO) {

    try {
      scheduleService.createSchedule(tid, user.getUid(), registerScheduleDTO, scheduleThumbnail, uploadPath);

    } catch (IllegalArgumentException | IOException e) {

      return "redirect:/planners/schedule/invite?createError="
          + URLEncoder.encode(
          e.getMessage(),
          StandardCharsets.UTF_8);
    }

    return "redirect:/planners/schedule?tid=" + tid;
  }

  @GetMapping("editor")
  public String editon(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid, Model model,
                       PageRequestDTO pageRequestDTO, @RequestParam("sid") Long sid) {

    // PageResultDTO<ResponseScheduleDTO, Schedule> result = scheduleService.getScheduleList(tid, pageRequestDTO);  findyBySid

    getPlannersDTO(tid, model, user);
    //스케쥴 정보 넘김
    Long uid = (user != null) ? user.getUid() : null;
    ResponseScheduleDTO schedule = scheduleService.get(sid, uid);
    if (schedule.getStatus().equals(ScheduleStatus.SCHEDULED)) {
      return "redirect:/planners/schedule/detail?tid=" + tid + "&sid=" + sid;
    }
    model.addAttribute("schedule", schedule);
    //주인장인지 보내주기
    model.addAttribute("isCreator", scheduleService.isCreator(sid, uid));
    System.out.println(scheduleService.isCreator(sid, uid));
    // 여기에 scheduleRepo에서 sid로 find한 모델전송
    model.addAttribute("sid", sid);
    model.addAttribute("tid", tid);
    return prefix + "editor";
  }

  @GetMapping("detail")
  public String detail(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid, Model model,
                       PageRequestDTO pageRequestDTO, @RequestParam("sid") Long sid) {

    // PageResultDTO<ResponseScheduleDTO, Schedule> result = scheduleService.getScheduleList(tid, pageRequestDTO);  findyBySid
    //기본
    getPlannersDTO(tid, model, user);
    //스케쥴 정보 넘김
    Long uid = (user != null) ? user.getUid() : null;
    ResponseScheduleDTO schedule = scheduleService.get(sid, uid);
    if (schedule.getStatus().equals(ScheduleStatus.PENDING)) {
      return "redirect:/planners/schedule/editor?tid=" + tid + "&sid=" + sid;
    }
    model.addAttribute("schedule", schedule);
    model.addAttribute("blocks", scheduleService.getScheduleBlockList(sid));
    //주인장인지 보내주기
    model.addAttribute("isCreator", scheduleService.isCreator(sid, uid));
    System.out.println(scheduleService.isCreator(sid, uid));
    // 여기에 scheduleRepo에서 sid로 find한 모델전송
    model.addAttribute("sid", sid);
    model.addAttribute("tid", tid);

    return prefix + "detail";
  }

  @PostMapping("/submit")
  @ResponseBody
  public ResponseEntity<Void> submitSchedule(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("sid") Long sid,
                                             @RequestBody List<ScheduleBlockDTO> scheduleBlockDTO) {
    Long result = scheduleService.submitSchedule(sid, scheduleBlockDTO);
    if (result == 1L) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else if (result == -1L) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  //일정 참가하기
  @GetMapping("/participate/{sid}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> participate(@AuthenticationPrincipal AuthUserDTO user, @PathVariable("sid") String sid) {
    Map<String, Object> result = new HashMap<>();

    result.put("message", sid + "번 참여자가 등록되었습니다.");

    // 200 OK와 함께 JSON 반환
    return ResponseEntity.ok(result);
  }

  // 일정 관리 페이지 : 방장
  // @GetMapping("admin")

  // 일정 확정 페이지 : 오늘 이후의 !
  // @GetMapping("print")

  // 일정 지우기
  @DeleteMapping("")
  @ResponseBody
  public ResponseEntity<Void> removeSchedule(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("sid") Long sid) {
    Long uid = (user != null) ? user.getUid() : null;
    Long result = scheduleService.removeSchedule(sid, uid);
    if (result == 1L) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else if (result == 0L) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
