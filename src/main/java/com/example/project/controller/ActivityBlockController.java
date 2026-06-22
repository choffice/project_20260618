package com.example.project.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.project.dto.ActivityBlockDTO;
import com.example.project.service.ActivityBlockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/planners/activity")
public class ActivityBlockController {
  private final ActivityBlockService activityBlockService;

  @GetMapping("")
  public ResponseEntity<List<ActivityBlockDTO>> getActivityBlockList(@RequestParam("tid") Long tid) {
    List<ActivityBlockDTO> result = activityBlockService.getActivityBlockList(tid);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<Void> addActivityBlock(@RequestParam("tid") Long tid, @RequestParam("name") String name) {
    Long result = activityBlockService.addActivityBlock(tid, name);
    if (result == 1L) {
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @DeleteMapping("")
  public ResponseEntity<Void> removeActivityBlock(@RequestParam("bid") Long bid) {
    Long result = activityBlockService.removeActivityBlock(bid);
    if (result == 1L) {
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
}
