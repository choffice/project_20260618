package com.example.project.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.project.dto.FriendDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.security.dto.AuthUserDTO;
import com.example.project.service.FriendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

  private final FriendService friendService;

  // 친구 목록, 친구 요청 목록 조회
  @GetMapping("")
  public String friends(@AuthenticationPrincipal AuthUserDTO user,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        @RequestParam(value = "tab", required = false) String tab , Model model) {
    Long uid = user != null ? user.getUid() : null;

    List<FriendDTO> friendList;
    List<FriendDTO> requestList;

    if (keyword != null && !keyword.isBlank()) {
      if ("request".equals(tab)) {
        friendList = friendService.getFriendList(uid);
        requestList = friendService.searchFriendRequestList(uid, keyword);
      } else {
        friendList = friendService.searchFriend(uid, keyword);
        requestList = friendService.getFriendRequestList(uid);
      }
    } else {
      friendList = friendService.getFriendList(uid);
      requestList = friendService.getFriendRequestList(uid);
    }

    model.addAttribute("friendList", friendList);
    model.addAttribute("requestList", requestList);
    model.addAttribute("tab", tab);
    model.addAttribute("keyword", keyword);

    return "user/friends";
  }

  // 친구 추가 요청
  @PostMapping("/add")
  @ResponseBody
  public ResponseEntity<Void> addFriend(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("receiver") Long receiver) {
    Long uid = user != null ? user.getUid() : null;

    Long result = friendService.addFriend(uid, receiver);
    if (result == 1L) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else if (result == -1L) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    } else if (result == -2L) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 친구 요청 수락
  @PostMapping("/accept")
  @ResponseBody
  public ResponseEntity<Void> acceptRequest(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("request") Long request) {
    Long uid = user != null ? user.getUid() : null;

    Long result = friendService.acceptRequest(request, uid);
    if (result == 1L) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else if (result == 0L) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  // 친구 요청 거절
  @PostMapping("/decline")
  @ResponseBody
  public ResponseEntity<Void> declineRequest(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("request") Long request) {
    Long uid = user != null ? user.getUid() : null;

    Long result = friendService.declineRequest(request, uid);
    if (result == 1L) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else if (result == 0L) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 친구 삭제
  @DeleteMapping("")
  @ResponseBody
  public ResponseEntity<Void> removeFriend(@AuthenticationPrincipal AuthUserDTO user, @RequestParam("ffid") Long ffid) {
    Long uid = user != null ? user.getUid() : null;

    Long result = friendService.removeFriend(ffid);
    if (result == 1L) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else if (result == 0L) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
