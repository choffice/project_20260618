package com.example.project.controller;

import com.example.project.dto.*;
import com.example.project.repository.UserRepository;
import com.example.project.security.dto.AuthUserDTO;
import com.example.project.service.PlannersService;
import com.example.project.service.UserService;
import com.example.project.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.web.client.RestTemplate; // RestTemplate
import tools.jackson.core.type.TypeReference; // 제네릭 타입 참조
import tools.jackson.databind.ObjectMapper; // 핵심 Mapper

@Controller
@RequiredArgsConstructor
public class RealtimeController {
  private final UserRepository userRepository;
  private final PlannersService plannersService;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserService userService;

  private final Map<Long, Map<String, TaskMoveDTO>> activeScheduledTasks = new ConcurrentHashMap<>();

  private Map<String, TaskMoveDTO> getActiveScheduledTasks(Long sid) {
    return activeScheduledTasks.computeIfAbsent(sid, key -> new ConcurrentHashMap<>());
  }

  private Map<String, String> pageNamed = new HashMap<>() {
    {
      put("/", "메인에서 노는 중");
      put("/user/login", "로그인 중");
      put("/user/register", "회원가입 중");
      put("/planners", "플래너즈 메인");
      put("/planners/list", "플래너즈 둘러보는 중");
      put("/planners/member", "플래너즈 인간 세는 중");
      put("/planners/create", "플래너즈 만드는 중");
      put("/planners/admin", "플래너즈 관리 중");
      put("/planners/schedule", "일정 살피는 중");
      put("/planners/schedule/invite", "일정 생성 중");
      put("/planners/review", "리뷰 확인 중");
      put("/planners/schedule/editor", "일정시간표 편집 중");
      put("/planners/schedule/detail", "일정시간표 보는 중");
      put("/planners/reviewForm", "리뷰 작성 중");
    }
  };

  // 날짜 함수
  public String parseDate(String input) {
    if (input == null || input.trim().isEmpty()) {
      return null; // 입력이 없으면 null
    }
    // 앞뒤 + 중간 공백 제거
    input = input.trim().replaceAll("\\s+", "");
    LocalDate today = LocalDate.now();
    LocalDate targetDate = null;

    // 오늘 / 내일
    if (input.equals("오늘")) {
      targetDate = today;
    } else if (input.equals("내일")) {
      targetDate = today.plusDays(1);
    }
    // 0월0일 패턴
    else if (input.matches("\\d{1,2}월\\d{1,2}일")) {
      Pattern p = Pattern.compile("(\\d{1,2})월(\\d{1,2})일");
      Matcher m = p.matcher(input);
      if (m.find()) {
        int month = Integer.parseInt(m.group(1));
        int day = Integer.parseInt(m.group(2));
        targetDate = LocalDate.of(today.getYear(), month, day);
      }
    }
    // 0.0 패턴 (월.일)
    else if (input.matches("\\d{1,2}\\.\\d{1,2}")) {
      String[] parts = input.split("\\.");
      int month = Integer.parseInt(parts[0]);
      int day = Integer.parseInt(parts[1]);
      targetDate = LocalDate.of(today.getYear(), month, day);
    }
    // 0/0 패턴 (월/일)
    else if (input.matches("\\d{1,2}/\\d{1,2}")) {
      String[] parts = input.split("/");
      int month = Integer.parseInt(parts[0]);
      int day = Integer.parseInt(parts[1]);

      try {
        targetDate = LocalDate.of(today.getYear(), month, day);
      } catch (DateTimeException e) {
        return null;
      }
    }
    // 연도 포함 (예: 27년6월20일, 2027년6월20일)
    else if (input.matches("\\d{2,4}년\\d{1,2}월\\d{1,2}일")) {
      Pattern p = Pattern.compile("(\\d{2,4})년(\\d{1,2})월(\\d{1,2})일");
      Matcher m = p.matcher(input);
      if (m.find()) {
        int year = Integer.parseInt(m.group(1));
        if (year < 100) { // 2자리 연도 처리
          year += 2000;
        }
        int month = Integer.parseInt(m.group(2));
        int day = Integer.parseInt(m.group(3));
        targetDate = LocalDate.of(year, month, day);
      }
    } // 8자리 날짜 패턴
    else if (input.matches("\\d{8}")) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      targetDate = LocalDate.parse(input, formatter);
    } // 4자리 날짜 패턴
    else if (input.matches("\\d{4}")) {
      int month = Integer.parseInt(input.substring(0, 2));
      int day = Integer.parseInt(input.substring(2, 4));

      // 0000 같은 잘못된 날짜 방어
      if (month < 1 || month > 12 || day < 1 || day > 31) {
        return null;
      }

      targetDate = LocalDate.of(today.getYear(), month, day);
    }

    // 매칭된 게 없으면 null 반환
    if (targetDate == null) {
      return null;
    }

    // yyyyMMdd 포맷으로 반환
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    return targetDate.format(formatter);
  }

  private Map<String, String> stationMap = Map.ofEntries(
      Map.entry("서울", "11B10101"),
      Map.entry("인천", "11B20201"),
      Map.entry("수원", "11B20601"),
      Map.entry("춘천", "11D10301"),
      Map.entry("강릉", "11D20501"),
      Map.entry("청주", "11C10301"),
      Map.entry("대전", "11C20401"),
      Map.entry("전주", "11F10201"),
      Map.entry("광주", "11F20501"),
      Map.entry("목포", "21F20801"),
      Map.entry("대구", "11H10701"),
      Map.entry("포항", "11H10201"),
      Map.entry("울산", "11H20101"),
      Map.entry("부산", "11H20201"),
      Map.entry("제주", "11G00201"));

  private Map<String, String> weatherImg = Map.of(
      "맑음", "https://cdn.pixabay.com/photo/2026/01/27/17/27/hills-10091311_1280.png",
      "더움", "https://cdn.pixabay.com/photo/2017/02/16/17/47/icon-2071969_1280.png",
      "추움", "https://cdn.pixabay.com/photo/2013/07/13/10/10/cold-156666_1280.png",
      "흐림", "https://cdn.pixabay.com/photo/2013/04/01/09/22/clouds-98536_1280.png",
      "비옴", "https://cdn.pixabay.com/photo/2016/03/31/20/31/green-1295839_1280.png",
      "폭우", "https://cdn.pixabay.com/photo/2013/07/12/15/03/clouds-149344_1280.png",
      "눈내림", "https://cdn.pixabay.com/photo/2022/12/21/12/42/christmas-card-7669974_1280.png");

  // 날씨 그림으로 반환
  public String classifyWeather(String[] cols, String wf) {
    double temp = parse(cols[12]); // 기온
    double rainProb = parse(cols[13]); // 강수확률

    String skyCode = cols[14];
    String prepCode = cols[15];
    String weather;

    // 강수 종류 우선
    if ("3".equals(prepCode)) {
      weather = "눈내림";
    } else if ("1".equals(prepCode) || "2".equals(prepCode) || "4".equals(prepCode)) {
      weather = rainProb >= 70 ? "폭우" : "비옴";
    } else {
      weather = switch (skyCode) {
        case "DB01", "DB02" -> "맑음";
        case "DB03", "DB04" -> "흐림";
        default -> "흐림";
      };

      // 온도 보정 (기온 정보 있을 때만)
      if (!Double.isNaN(temp)) {
        if (temp >= 30) {
          weather = "더움";
        } else if (temp <= 0) {
          weather = "추움";
        }
      }
    }

    return wf
        + "<br><img style=\"width:150px;\" src=\""
        + weatherImg.getOrDefault(weather, weatherImg.get("흐림"))
        + "\">"
        + "<br>🌡️ 기온 : "
        + (Double.isNaN(temp)
            ? "정보 없음"
            : ((int) temp) + "℃")
        + "<br>☔ 강수확률 : "
        + (Double.isNaN(rainProb)
            ? "정보 없음"
            : ((int) rainProb) + "%");
  }

  private double parse(String value) {
    try {
      double d = Double.parseDouble(value);
      // 기상청 결측치
      if (d == -9.0 || d == -99.0) {
        return Double.NaN;
      }

      return d;
    } catch (Exception e) {
      return Double.NaN;
    }
  }

  @GetMapping("/chat")
  public String chatPage(@AuthenticationPrincipal AuthUserDTO user, Model model) {
    model.addAttribute("username", user.getName());
    return "chat";
  }

  @MessageMapping("/chat.status")
  @SendTo("/topic/status")
  public ResponseEntity<ChatStatusDTO> sendNotification(ChatStatusDTO message, ResponseUserDTO responseUserDTO) {
    // 이메일로 값 보내기
    if (message.getChatterName().matches("^([a-z])\\1{2}@\\1{3}$")) {
      ResponseUserDTO fakeuser = userService.searchUsersByEmail(message.getChatterName()).get(0);
      message.setChatterId(fakeuser.getUid());
      message.setChatter(fakeuser.getName());
    }

    Long myId = message.getChatterId() != null ? message.getChatterId() : 0L;
    String profileImg = ""; /* 기본 */

    String userLoc = message.getChatterLoc();
    if (userLoc.contains("planners?tid=66"))
      message.setChatterLoc("피카배구 하는 중");
    else if (userLoc.contains("planners?tid=67"))
      message.setChatterLoc("게임 둘러보는 중");
    else if (pageNamed.get(userLoc.split("\\?")[0]) != null) {
      userLoc = pageNamed.get(userLoc.split("\\?")[0]);
      message.setChatterLoc(userLoc);
    }

    if (myId != 0L) {
      profileImg = userRepository.findById(myId).get().getProfileImg();
      message.setProfileImg(profileImg); // 서버에서 채워 넣음
    }
    return ResponseEntity.ok(message);
  }

  @MessageMapping("/chat.send")
  @SendTo("/topic/chatroom")
  public Map<String, Object> sendChat(ChatDTO message, ResponseUserDTO responseUserDTO) {
    // 이메일로 값 보내기
    if (message.getSender().matches("^([a-z])\\1{2}@\\1{3}$")) {
      ResponseUserDTO fakeuser = userService.searchUsersByEmail(message.getSender()).get(0);
      message.setSenderId(fakeuser.getUid());
      message.setSender(fakeuser.getName());
    }

    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> userMsg = new HashMap<>();
    Long myId = 0L; // 0은 시스템 알림

    if (message.getSenderId() != null)
      myId = message.getSenderId();

    userMsg.put("uid", myId);

    String stationCode = "11H20201"; // 기본 부산
    String forWth = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 기본 오늘

    // 날씨 처리
    if (message.getContent().equals("/날씨")
        || message.getContent().startsWith("/날씨 ")
        || message.getContent().startsWith("/wth ")) {
      String[] args = message.getContent().trim().split("\\s+");
      for (int i = 1; i < args.length; i++) {
        String stn = stationMap.get(args[i]);
        if (stn != null) {
          stationCode = stn;
          continue;
        }
        String parsed = parseDate(args[i]);
        if (parsed != null) {
          forWth = parsed;
        }
      }

      String urlStr = "https://apihub.kma.go.kr/api/typ01/url/fct_afs_dl.php"
          + "?reg=" + stationCode
          + "&tmfc=0"
          + "&disp=0"
          + "&help=1"
          + "&authKey=-EjxV7U9QHKI8Ve1PSByiw";
      System.out.println(">> " + urlStr);
      try {
        String targetDate = forWth;
        // System.out.println(forWth);

        String response = restTemplate.getForObject(urlStr, String.class);
        String dataLine = Arrays.stream(response.split("\n"))
            .map(String::trim)
            .filter(s -> !s.startsWith("#"))
            .filter(s -> !s.isBlank())
            .filter(s -> {
              String[] cols = s.split("\\s+");
              if (cols.length < 3 || targetDate == null)
                return false;
              return cols[2].startsWith(targetDate);
            })
            .findFirst()
            .orElse(null);
        // System.out.println(">>>");
        // System.out.println(response);
        if (dataLine != null) {
          String[] cols = dataLine.trim().split("\\s+");
          String wf = dataLine.substring(
              dataLine.indexOf("\"") + 1,
              dataLine.lastIndexOf("\""));

          System.out.println("forWth = " + forWth);
          System.out.println("targetDate = " + targetDate);
          System.out.println("dataLine = " + dataLine);

          String stationKey = "현재 지역";
          for (Map.Entry<String, String> entry : stationMap.entrySet()) {
            if (Objects.equals(entry.getValue(), stationCode)) {
              stationKey = entry.getKey();
            }
          }
          message.setContent(classifyWeather(cols, forWth + " " + stationKey + " 날씨 : " + wf.replace("\"", "")));
        }
      } catch (Exception e) {
        e.printStackTrace();
        message.setContent("날씨 정보를 찾을 수 없습니다.");
      }
    }

    userMsg.put("userMsg", message);
    if (myId != 0L)
      userMsg.put("userProfile", userRepository.findById(myId).get().getProfileImg());

    return userMsg;
  }

  // 오창 : 에디터용 채팅 방식 20260618
  @MessageMapping("/chat.sent")
  public void sendChatSchedule(ChatDTO message) {
    Long myId = 0L; // 0은 시스템 알림

    ChatDTO state = new ChatDTO();

    if (message.getSenderId() != null)
      state.setSenderId(0L);
    state.setSid(message.getSid());
    state.setSender(message.getSender());
    state.setSenderId(message.getSenderId());
    state.setContent(message.getContent());
    state.setUserProfile(userService.searchUserByUid(message.getSenderId()).getProfileImg());
    messagingTemplate.convertAndSend("/topic/editorroom." + message.getSid(), state);
  }

  private final ScheduleService scheduleService;

  // 오창
  @MessageMapping("/chat.admin")
  public void socketControll(ChatSocketDTO message) {

    if (scheduleService.isCreator(message.getSid(), message.getUid()))// 관리자가 맞으면!
      message.setReqCode(false);// 끝! 닫을 때만 쓸거라

    messagingTemplate.convertAndSend("/topic/chatlive." + message.getSid(), message);
  }

  @MessageMapping("/task.move")
  public void moveTask(TaskMoveDTO message) {
    if (message.getSid() != null) {
      Map<String, TaskMoveDTO> stateMap = getActiveScheduledTasks(message.getSid());

      if ("REMOVE".equals(message.getAction())) {
        if (message.getInstanceId() != null && !message.getInstanceId().isBlank()) {
          stateMap.remove(message.getInstanceId());
        }
      } else if ("COPY".equals(message.getMode()) || "MOVE_SCHEDULED".equals(message.getMode())) {
        if (message.getInstanceId() != null && !message.getInstanceId().isBlank()) {
          TaskMoveDTO state = new TaskMoveDTO();
          state.setSid(message.getSid());
          state.setUserId(message.getUserId());
          state.setUsername(message.getUsername());
          state.setTaskId(message.getTaskId());
          state.setMode(message.getMode());
          state.setInstanceId(message.getInstanceId());
          state.setNewStatus(message.getNewStatus());
          state.setAction(message.getAction());
          state.setContent(message.getContent());
          state.setStartTime(message.getStartTime());
          state.setEndTime(message.getEndTime());
          state.setScheduleDate(message.getScheduleDate());

          stateMap.put(message.getInstanceId(), state);
        }
      }
    }
    messagingTemplate.convertAndSend("/topic/task." + message.getSid(), message);
  }

  @MessageMapping("/task.state.request")
  public void requestTaskState(TaskMoveDTO message) {
    if (message.getSid() == null)
      return;
    Map<String, TaskMoveDTO> stateMap = activeScheduledTasks.get(message.getSid());
    if (stateMap == null || stateMap.isEmpty())
      return;
    messagingTemplate.convertAndSend("/topic/task.state." + message.getSid(), new ArrayList<>(stateMap.values()));
  }

  @MessageMapping("/cursor.batch")
  public void cursorBatch(@Payload CursorBatchDTO payload) {
    messagingTemplate.convertAndSend("/topic/cursor.batch." + payload.getSid(), payload);
  }

  @MessageMapping("/task.drag.start")
  public void dragStart(DragStateDTO message) {
    message.setType("START");

    messagingTemplate.convertAndSend("/topic/task.drag." + message.getSid(), message);
  }

  @MessageMapping("/task.drag.move")
  public void dragMove(@Payload DragMoveDTO message) {
    messagingTemplate.convertAndSend("/topic/task.drag.move." + message.getSid(), message);
  }

  @MessageMapping("/task.drag.end")
  public void dragEnd(DragStateDTO message) {
    message.setType("END");
    /// 이부분
    messagingTemplate.convertAndSend("/topic/task.drag." + message.getSid(), message);
  }

  @MessageMapping("/task.remove")
  public void removeTask(TaskMoveDTO message) {
    message.setAction("REMOVE");
    if (message.getSid() != null && message.getInstanceId() != null && !message.getInstanceId().isBlank()) {
      Map<String, TaskMoveDTO> stateMap = activeScheduledTasks.get(message.getSid());
      if (stateMap != null) {
        stateMap.remove(message.getInstanceId());
      }
    }

    messagingTemplate.convertAndSend("/topic/task." + message.getSid(), message);
  }
}
