package com.example.project.service;

import com.example.project.dto.LoginDTO;
import com.example.project.dto.RegisterUserDTO;
import com.example.project.dto.ResponsePlannersDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.entity.User;
import com.example.project.entity.UserRole;
import com.example.project.entity.UserStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;

public interface UserService {
  Long registerUser(RegisterUserDTO registerUserDTO, MultipartFile profileImage, String uploadPath) throws IOException;

  ResponseUserDTO login(LoginDTO loginDTO);

  ResponseUserDTO searchUserByUid(Long uid);

  List<ResponseUserDTO> searchUsersByEmail(String email);

  ResponseUserDTO modifyUser(RegisterUserDTO registerUserDTO, MultipartFile profileFile, String uploadPath) throws IOException;

  default User dtoToEntity(RegisterUserDTO registerUserDTO) {
    User user = User.builder()
        .email(registerUserDTO.getEmail())
        .password(registerUserDTO.getPassword())
        .name(registerUserDTO.getName())
        .profileImg(registerUserDTO.getProfileImg())
        .location(registerUserDTO.getLocation())
        .category(registerUserDTO.getCategory())
        .status(UserStatus.ACTIVE)
        .build();
    user.addMemberRole(UserRole.USER);
    return user;
  }

  default ResponseUserDTO entityToDto(User user) {
    return ResponseUserDTO.builder()
        .uid(user.getUid())
        .email(user.getEmail())
        .name(user.getName())
        .profileImg(user.getProfileImg())
        .location(user.getLocation())
        .category(user.getCategory())
        .build();
  }
}
