package com.example.project.controller;

import com.example.project.dto.PageRequestDTO;
import com.example.project.dto.PageResultDTO;
import com.example.project.dto.RegisterPlannersDTO;
import com.example.project.dto.ResponsePlannersDTO;
import com.example.project.dto.ResponseScheduleDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.entity.Schedule;
import com.example.project.security.dto.AuthUserDTO;
import com.example.project.service.FavoriteService;
import com.example.project.service.PlannersReviewService;
import com.example.project.service.PlannersService;
import com.example.project.service.ScheduleService;
import com.example.project.service.UserService;
import com.example.project.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/planners")
public class PlannersController {

  @Value("${com.example.upload.path}")
  private String uploadPath;

  private final PlannersService plannersService;
  private final PlannersReviewService plannersReviewService;
  private final ScheduleService scheduleService;
  private final FavoriteService favoriteService;
  private final UserService userService;
  private final ApplicationService applicationService;

  // 플래너즈 목록 조회
  @GetMapping("/list")
  public String getPlannersList(Model model, PageRequestDTO pageRequestDTO, @AuthenticationPrincipal AuthUserDTO user) {
    Long uid = user != null ? user.getUid() : null;

    model.addAttribute("pageRequestDTO", pageRequestDTO);
    model.addAttribute("pageResultDTO", plannersService.getPlannersList(pageRequestDTO, uid));
    model.addAttribute("pageTitle", "플래너즈 찾아보기");
    model.addAttribute("listAction", "/planners/list");

    return "/planners/list";
  }

  // 가입한 플래너즈 조회
  @GetMapping("/mylist")
  public String getMyPlannersList(Model model, PageRequestDTO pageRequestDTO,
      @AuthenticationPrincipal AuthUserDTO user) {
    if (user == null) {
      return "redirect:/user/login";
    }

    model.addAttribute("pageRequestDTO", pageRequestDTO);
    model.addAttribute("pageResultDTO", plannersService.getMyPlannersList(pageRequestDTO, user.getUid()));
    model.addAttribute("pageTitle", "가입한 플래너즈");
    model.addAttribute("listAction", "/planners/my/list");
    model.addAttribute("showInvitationActions", false);

    return "/planners/myList";
  }

  // 초대받은 플래너즈 조회
  @GetMapping("/invited/list")
  public String getInvitedPlannersList(Model model, PageRequestDTO pageRequestDTO,
      @AuthenticationPrincipal AuthUserDTO user) {
    if (user == null) {
      return "redirect:/user/login";
    }

    model.addAttribute("pageRequestDTO", pageRequestDTO);
    model.addAttribute("pageResultDTO", plannersService.getInvitedPlannersList(pageRequestDTO, user.getUid()));
    model.addAttribute("pageTitle", "초대 받은 플래너즈");
    model.addAttribute("listAction", "/planners/invited/list");
    model.addAttribute("showInvitationActions", true);

    return "/planners/myList";
  }

  // 내가 만든 플래너즈 조회
  @GetMapping("/created/list")
  public String getCreatedPlannersList(Model model, PageRequestDTO pageRequestDTO,
      @AuthenticationPrincipal AuthUserDTO user) {
    if (user == null) {
      return "redirect:/user/login";
    }

    model.addAttribute("pageRequestDTO", pageRequestDTO);
    model.addAttribute("pageResultDTO", plannersService.getCreatedPlannersList(pageRequestDTO, user.getUid()));
    model.addAttribute("pageTitle", "내가 만든 플래너즈");
    model.addAttribute("listAction", "/planners/created/list");
    model.addAttribute("showInvitationActions", false);

    return "/planners/myList";
  }

  // 찜한 플래너즈 조회
  @GetMapping("/favorite/list")
  public String getFavoritePlannersList(Model model, PageRequestDTO pageRequestDTO,
      @AuthenticationPrincipal AuthUserDTO user) {
    if (user == null) {
      return "redirect:/user/login";
    }

    model.addAttribute("pageRequestDTO", pageRequestDTO);
    model.addAttribute("pageResultDTO", plannersService.getFavoritePlannersList(pageRequestDTO, user.getUid()));
    model.addAttribute("pageTitle", "찜한 플래너즈");
    model.addAttribute("listAction", "/planners/favorite/list");
    model.addAttribute("showInvitationActions", false);

    return "/planners/myList";
  }

  // 플래너즈 생성 페이지
  @GetMapping("/create")
  public String createPlanners(@AuthenticationPrincipal AuthUserDTO user, Model model) {

    return "/planners/create";
  }

  // 플래너즈 생성
  @PostMapping("/create")
  public String createPlanners(@AuthenticationPrincipal AuthUserDTO user, Model model,
      RegisterPlannersDTO registerPlannersDTO,
      @RequestParam(value = "plannersThumbnailFile", required = false) MultipartFile plannersThumbnail,
      @RequestParam(value = "plannersBannerFile", required = false) MultipartFile plannersBanner,
      RedirectAttributes ra) {
    try {
      Long newPlanners = plannersService.createPlanners(registerPlannersDTO, user.getUid(), plannersThumbnail,
          plannersBanner, uploadPath);
      ra.addAttribute("tid", newPlanners);
    } catch (IllegalArgumentException | IOException e) {
      return "redirect:/planners/create?createError=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
    }

    return "redirect:/planners";
  }

  // 인기 플래너즈 조회
  @GetMapping("/popular")
  @ResponseBody
  public ResponseEntity<List<ResponsePlannersDTO>> getPopularPlannersList(@AuthenticationPrincipal AuthUserDTO user) {
    Long uid = user != null ? user.getUid() : null;

    return new ResponseEntity<>(plannersService.getPopularPlanners(uid), HttpStatus.OK);
  }

  // 추천 플래너즈 조회
  @GetMapping("/recommend")
  @ResponseBody
  public ResponseEntity<List<ResponsePlannersDTO>> getRecommendedPlannersList(
      @AuthenticationPrincipal AuthUserDTO user) {
    Long uid = user != null ? user.getUid() : null;

    return new ResponseEntity<>(plannersService.getRecommendedPlanners(uid), HttpStatus.OK);
  }

  // 플래너즈 세부정보 페이지
  @GetMapping
  public String getPlannersDetail(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid,
      Model model, PageRequestDTO pageRequestDTO) {
    return getPlannersDTO(tid, model, "detail", user, pageRequestDTO);
  }

  // 플래너즈 페이지 종합 메서드
  private String getPlannersDTO(Long tid, Model model, String suffix, AuthUserDTO user, PageRequestDTO pageRequestDTO) {
    Long uid = (user != null) ? user.getUid() : null;
    String email = (user != null) ? user.getEmail() : null;
    // 플래너즈 정보
    ResponsePlannersDTO responsePlannersDTO = plannersService.getPlannersById(tid);
    if (responsePlannersDTO != null) {
      model.addAttribute("plannersModifyThumbnail", responsePlannersDTO.getPlannersThumbnail());
      model.addAttribute("plannersModifyBanner", responsePlannersDTO.getPlannersBanner());

      if (responsePlannersDTO.getPlannersBanner().equals("/img/banner.png"))
        responsePlannersDTO.setPlannersBanner(
            "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1800&q=80");
      if (responsePlannersDTO.getPlannersThumbnail().equals("/img/thumbnail.png"))
        responsePlannersDTO.setPlannersThumbnail("/img/empty_thumbnail.png");
      model.addAttribute("plannersDTO", responsePlannersDTO);
      model.addAttribute("pageRequestDTO", pageRequestDTO);
      model.addAttribute("currentUserUid", uid);
      model.addAttribute("currentUserEmail", email);
      model.addAttribute("owner", plannersService.getOwner(tid));
      model.addAttribute("isMember", plannersService.isMember(tid, uid));
      model.addAttribute("previewMembers", plannersService.getPreviewMembers(tid));
      model.addAttribute("memberCount", plannersService.getMemberCount(tid));
      // model.addAttribute("", plannersService.memberList(tid));
      // 좋아요 조회
      if (uid != null && favoriteService.get(uid).contains(tid))
        model.addAttribute("active", "active");
      return "planners/" + suffix;
    }
    return "redirect:/planners/list";
  }

  // 플래너즈 가입 페이지
  @GetMapping("/join")
  public String joinPlanners(Model model, @AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid,
      RedirectAttributes ra) {
    // 사용자가 로그인 상태이며 (null 아님), 이미 가입된 멤버인지 확인
    if (user != null && plannersService.isMember(tid, user.getUid())) {
      // 이미 가입된 회원이면 플래너즈 상세 페이지로 강제 리다이렉트
      return "redirect:/planners?tid=" + tid;
    }

    String formSchema = applicationService.getForm(tid);
    if (formSchema == null || "[]".equals(formSchema) || formSchema.isEmpty()) {
      ra.addFlashAttribute("toastMessage", "칫, 결계인가.");
      return "redirect:/planners?tid=" + tid;
    }

    model.addAttribute("tid", tid);
    return "/planners/join";
  }

  //플래너즈 탈퇴 페이지
  @PostMapping("/leave/{tid}")
  @ResponseBody
  public ResponseEntity<Long> leavePlanners(@AuthenticationPrincipal AuthUserDTO user, @PathVariable("tid") Long tid) {
    Long result = 0L;
    System.out.println("탈퇴 메서드 실행");
    // 사용자가 로그인 상태이며 (null 아님), 이미 가입된 멤버인지 확인
    if (user != null && plannersService.isMember(tid, user.getUid())) {
      // 이미 가입된 회원이면 플래너즈 상세 페이지로 강제 리다이렉트
      result = plannersService.deleteUserFromPlanners(tid, user.getUid());
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 플래너즈 멤버 목록 조회
  @GetMapping("/member")
  public String getMemberAll(@AuthenticationPrincipal AuthUserDTO user,
      @RequestParam("tid") Long tid, Model model,
      PageRequestDTO pageRequestDTO) {

    return getPlannersDTO(tid, model, "member", user, pageRequestDTO);
  }

  // 플래너즈 리뷰 목록 조회
  @GetMapping("/review")
  public String getReviewAll(@AuthenticationPrincipal AuthUserDTO user,
      @RequestParam("tid") Long tid, Model model, PageRequestDTO pageRequestDTO) {
    Long uid = (user != null) ? user.getUid() : null; // null-safe 처리
    model.addAttribute("reviewList", plannersReviewService.getReviewList(tid));
    model.addAttribute("hasReview", plannersReviewService.hasReview(tid, uid));
    return getPlannersDTO(tid, model, "review", user, pageRequestDTO);
  }

  // 플래너즈 리뷰 작성 처리
  @PostMapping("/reviewForm")
  public String createReview(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid, 
                             @RequestParam("reviewTitle") String reviewTitle, @RequestParam("rating") Integer rating,
                             @RequestParam("content") String content) {
    plannersReviewService.createReview(tid, user.getUid(), reviewTitle, rating, content);
        
    return "redirect:/planners/review?tid=" + tid;
  }

  // 플래너즈 리뷰 수정 처리
  @PostMapping("/reviewModify")
  public String modifyReview(
    @AuthenticationPrincipal AuthUserDTO user, 
    @RequestParam("rid") Long rid, 
    @RequestParam("tid") Long tid, 
    @RequestParam("reviewTitle") String reviewTitle, 
    @RequestParam("rating") Integer rating,
    @RequestParam("content") String content
  ){
    plannersReviewService.modifyReview(
      rid, user.getUid(), reviewTitle, rating, content
    );
        
    return "redirect:/planners/review?tid=" + tid;
  }

  // 플래너즈 리뷰 작성 페이지 이동
  @GetMapping("/reviewForm")
    public String getReviewForm(@AuthenticationPrincipal AuthUserDTO user, 
      @RequestParam("tid") Long tid, Model model, PageRequestDTO pageRequestDTO) {
        
      Long uid = (user != null) ? user.getUid() : null;
      if (user != null && plannersReviewService.hasReview(tid, uid)) {
      return "redirect:/planners/review?tid=" + tid;
    }

    return getPlannersDTO(tid, model, "reviewForm", user, pageRequestDTO);
  }

  // 플래너즈 리뷰 삭제 처리
  @DeleteMapping("/review/{rid}")
  public ResponseEntity<Void> deleteReview(@PathVariable("rid") Long rid, @AuthenticationPrincipal AuthUserDTO user) {
    try {
      plannersReviewService.deleteReview(rid, user.getUid());
    } catch (Exception e) {
      System.out.println("리뷰 삭제 중 오류 발생: " + e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // 플래너즈 관리자 페이지
  @GetMapping("/admin")
  public String getAdminPage(@AuthenticationPrincipal AuthUserDTO user,
      @RequestParam("tid") Long tid, Model model, PageRequestDTO pageRequestDTO) {

    return getPlannersDTO(tid, model, "admin", user, pageRequestDTO);
  }

  // 플래너즈 수정
  @PostMapping("/modify")
  public String modifyPlanners(@AuthenticationPrincipal AuthUserDTO user, Model model,
      RegisterPlannersDTO registerPlannersDTO,
      @RequestParam(value = "plannersThumbnailFile", required = false) MultipartFile plannersThumbnail,
      @RequestParam(value = "plannersBannerFile", required = false) MultipartFile plannersBanner,
      RedirectAttributes ra) {
    try {
      Long newPlanners = plannersService.modifyPlanners(registerPlannersDTO, user.getUid(), plannersThumbnail,
          plannersBanner, uploadPath);
      ra.addAttribute("tid", newPlanners);
    } catch (IllegalArgumentException | IOException e) {
      return "redirect:/planners/admin/modify?modifyError=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
    }

    return "redirect:/planners";
  }

  // 플래너즈 삭제
  @PostMapping("/delete")
  @ResponseBody
  public ResponseEntity<Void> deletePlanners(@RequestParam("tid") Long tid) {
    try {
      plannersService.deletePlanners(tid);
    } catch (Exception e) {
      System.out.println("플래너즈 삭제 중 오류 발생: " + e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // 플래너즈에 초대할 이메일 검색
  @GetMapping("/search_invitee")
  @ResponseBody
  public ResponseEntity<List<ResponseUserDTO>> searchUser(@RequestParam("email") String email) {
    List<ResponseUserDTO> users = userService.searchUsersByEmail(email);
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  // 플래너즈에 초대
  @PostMapping("/invite")
  @ResponseBody
  public ResponseEntity<Void> inviteUser(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("tid") Long tid,
      @RequestParam("email") String email) {
    Long uid = user != null ? user.getUid() : null;

    Long result = plannersService.inviteUser(uid, tid, email);
    if (result == -2L) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } else if (result == -1L || result == 0L) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // 초대 수락
  @PostMapping("/invited/accept")
  @ResponseBody
  public ResponseEntity<String> acceptInvitation(@AuthenticationPrincipal AuthUserDTO user,
      @RequestParam("tid") Long tid) {
    Long uid = user != null ? user.getUid() : null;

    Long result = plannersService.acceptInvitation(uid, tid);
    if (result == 1L) {
      return new ResponseEntity<>("ACCEPT", HttpStatus.OK);
    } else if (result == 0L) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  // 초대 거절
  @PostMapping("/invited/decline")
  @ResponseBody // Rest 방식
  public ResponseEntity<String> declineInvitation(@AuthenticationPrincipal AuthUserDTO user,
      @RequestParam("tid") Long tid) {
    Long uid = user != null ? user.getUid() : null;

    Long result = plannersService.declineInvitation(uid, tid);
    if (result == 1L) {
      return new ResponseEntity<>("DECLINE", HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @GetMapping("/api/members/{tid}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> getMembersApi(@PathVariable("tid") Long tid, 
                                                           @RequestParam(value = "page", defaultValue = "1") int page,
                                                           Model model) {
    // 기존에 멤버 리스트를 가져오던 서비스 로직을 그대로 사용
    Map<String, Object> result = plannersService.getPlannersUserByTid(tid, page);
    
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
