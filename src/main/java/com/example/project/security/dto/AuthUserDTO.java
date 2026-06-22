package com.example.project.security.dto;

import com.example.project.entity.UserStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Log4j2
@Getter
@Setter
@ToString
public class AuthUserDTO extends User {
  // User를 상속 받았기에 세션으로 저장 가능, session 정보를 저장할 때 필요한 객체
  private Long uid;
  private String email;
  private String password;
  private String name;
  private String profileImg;
  private String location;
  private String category;
  private UserStatus status;

  public AuthUserDTO(String username, @Nullable String password, Collection<? extends GrantedAuthority> authorities, Long uid, String email, String name, String profileImg, String location, String category, UserStatus status) {
    super(username, password, authorities);   // 이 정보는 반드시 User로 전송
    this.uid = uid;
    this.email = email;
    this.password = password;
    this.name = name;
    this.profileImg = profileImg;
    this.location = location;
    this.category = category;
    this.status = status;
  }
}
