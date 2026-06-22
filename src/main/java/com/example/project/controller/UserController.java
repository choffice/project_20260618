package com.example.project.controller;

import com.example.project.dto.RegisterUserDTO;
import com.example.project.dto.ResponsePlannersDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.security.dto.AuthUserDTO;
import com.example.project.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  @Value("${com.example.upload.path}")
  private String uploadPath;

  private final UserService userService;

  // 로그인 페이지
  @GetMapping("/login")
  public String login(@RequestParam(value = "error", required = false) String error, Model model) {
    if (error != null) {
      model.addAttribute("errorMessage", "이메일 또는 비밀번호를 확인해주세요.");
    }
    return "user/login";
  }

  // 회원가입 페이지
  @GetMapping("/register")
  public String register() {
    return "user/register";
  }

  // 회원가입
  @PostMapping("/register")
  public String register(RegisterUserDTO registerUserDTO, @RequestParam(value = "error", required = false) String error,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
    try {
      userService.registerUser(registerUserDTO, profileImage, uploadPath);
      return "redirect:/user/login";
    } catch (IllegalArgumentException | IOException e) {
      return "redirect:/user/register?registerError=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
    }
  }

  // 로그아웃
  @GetMapping("/logout")
  public String logout() {
    return "user/logout";
  }

  // 마이페이지
  @GetMapping("/mypage")
  public String myPage(@AuthenticationPrincipal AuthUserDTO user, Model model) {
    if (user != null) {
      model.addAttribute("user", user);
    }

    return "user/mypage";
  }

  // 회원정보 수정 페이지
  @GetMapping("/modify")
  public String modifyForm(@AuthenticationPrincipal AuthUserDTO user, Model model) {
    if (user != null) {
      model.addAttribute("user", user);
    }

    return "user/modify";
  }

  // 회원정보 수정
  @PostMapping("/modify")
  public String modify(RegisterUserDTO registerUserDTO, @RequestParam(value = "profileFile", required = false)
                       MultipartFile profileFile, @AuthenticationPrincipal AuthUserDTO user,
                       RedirectAttributes redirectAttributes) {
    try {
      ResponseUserDTO updatedUser =
              userService.modifyUser(registerUserDTO, profileFile, uploadPath);

      if (user != null) {
        user.setName(updatedUser.getName());
        user.setProfileImg(updatedUser.getProfileImg());
        user.setLocation(updatedUser.getLocation());
        user.setCategory(updatedUser.getCategory());
      }

      return "redirect:/user/mypage";

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("modifyError", e.getMessage());
      return "redirect:/user/modify";
    }
  }
}
