package com.example.project.config;

import com.example.project.security.handler.CustomAccessDeniedHandler;
import com.example.project.security.handler.CustomLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  // 개방하는 주소 목록
  private static final String[] PUBLIC_URLS = {
      "/", "/user/login/**", "/css/**", "/js/**", "/img/**"
  };

  // 인증이 필요한 주소 목록
  private static final String[] PRIVATE_URLS = {
      "/user/logout/**", "/mypage/**", "/planners/create/**", "/planners/join/**", "/accessDenied", "/chat/**", "/user/mypage/**"
  };

  @Bean   // SecurityFilterChain 설정시 모든 시큐리티 설정은 직접 지정해줘야 함
  protected SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception {
    // CSRF(Cross Site Request Forgery): 교차 사이트 요청 위조
    // httpSecurity.csrf(csrf -> csrf.disable());  // csrf을 사용 안할경우


    // authorizeHttpRequests: http의 요청에 대한 인증과 권한 처리
    httpSecurity.authorizeHttpRequests(auth -> {
      // 요청하는 각각의 주소에 대하여 접근을 설정
      // 주소 관련: requestMatchers(), anyRequest()
      // 인증 관련: permitAll(), denyAll(), authenticated()
      auth.requestMatchers(PUBLIC_URLS).permitAll();   // 개방주소 등록
      auth.requestMatchers(PRIVATE_URLS).authenticated();   // 로그인된 사용자만 사용 가능
      auth.anyRequest().permitAll();   // 모든 주소 허용, anyRequest는 반드시 마지막으로 와야함
    });


    // 사용자가 별도로 만든 "/login", "/logout" 사용, 별도의 컨트롤러와 인증권한, html 필요
    httpSecurity.formLogin(httpSecurityFormLoginConfigurer -> {
      // 커스텀 login 페이지는 controller에 사용자가 직접 등록
      httpSecurityFormLoginConfigurer.loginPage("/user/login")
          // 로그인 처리를 할때 ClubUserDetailsService를 가지고 인증처리
          // /login 은 주소를 임의로 지정함. form 태그에서 action에 똑같이 지정해야함
          .loginProcessingUrl("/login")  // login 처리 주소
          .successHandler(getLoginSuccessHandler())
          .failureUrl("/user/login?error=true");
    });

    httpSecurity.logout(httpLogoutConfigurer -> {
      // 커스텀 logout 페이지는 controller에 사용자가 직접 등록
      httpLogoutConfigurer.logoutUrl("/logout")  // logout 처리 주소
          .deleteCookies("JSESSIONID")  // 쿠키 제거
          .invalidateHttpSession(true)  // 세션 제거
          .clearAuthentication(true)  // 인증 정보 제거
          .logoutSuccessUrl("/");
    });

    // 예외 발생시
    httpSecurity.exceptionHandling(httpExceptionHandlingConfigurer -> {
      // 권한이 없을때 접근 불가 페이지 지정, AuthController 등록 필요
      httpExceptionHandlingConfigurer
          // .accessDeniedPage("/accessDenied")
          .accessDeniedHandler(getAccessDeniedHandler());
    });

    return httpSecurity.build();
  }

  @Bean
  public AccessDeniedHandler getAccessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  @Bean
  public AuthenticationSuccessHandler getLoginSuccessHandler() {
    return new CustomLoginSuccessHandler();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}