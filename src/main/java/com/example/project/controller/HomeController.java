package com.example.project.controller;

import com.example.project.security.dto.AuthUserDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping({"", "/"})
  public String home(Model model, @AuthenticationPrincipal AuthUserDTO user) {
    model.addAttribute("loginUser", user);
    return "index";
  }

  @GetMapping("accessDenied")
  public void auth() {

  }

  @GetMapping("test")
  public String test() {
    return "/test";
  }
}
