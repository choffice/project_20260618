package com.example.project.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
    String redirect = request.getParameter("redirect");

    if (savedRequest != null) {
      response.sendRedirect(savedRequest.getRedirectUrl());
      return;
    }

    if (redirect != null && !redirect.isBlank()) {
      response.sendRedirect(redirect);
      return;
    }

    response.sendRedirect("/");
  }
}