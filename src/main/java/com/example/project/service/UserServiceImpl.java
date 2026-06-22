package com.example.project.service;

import com.example.project.dto.LoginDTO;
import com.example.project.dto.RegisterUserDTO;
import com.example.project.dto.ResponsePlannersDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.entity.User;
import com.example.project.entity.Planners;
import com.example.project.repository.PlannersRepository;
import com.example.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final PlannersRepository plannersRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public ResponseUserDTO searchUserByUid(Long uid) {
    return entityToDto(userRepository.findById(uid).get());
  }

  public List<ResponseUserDTO> searchUsersByEmail(String email) {
    return userRepository.findByEmailContainingIgnoreCase(email).stream()
        .map(this::entityToDto)
        .toList();
  }

  public Long registerUser(RegisterUserDTO registerUserDTO, MultipartFile profileImage, String uploadPath) throws IOException {
    if (userRepository.existsByEmail(registerUserDTO.getEmail())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    if (userRepository.existsByName(registerUserDTO.getName())) {
      throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
    }

    if (profileImage != null && !profileImage.isEmpty() && !profileImage.getContentType().startsWith("image")) {
      throw new IllegalArgumentException("이미지 형식이 올바르지 않습니다.");
    }

    registerUserDTO.setProfileImg("/img/profile.png");

    if (!profileImage.isEmpty()) {
      // 실제 파일 이름
      String originalName = profileImage.getOriginalFilename();
      String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

      // 저장될 경로 생성: c:\\upload\\2026\\05\\18
      String folderPath = makeFolder(uploadPath);

      String uuid = UUID.randomUUID().toString();

      // 실제로 서버에 저장될 경로와 파일명을 가진 변수: c:\\uploads\\2026\\05\\18\\uuid_fileName
      String saveName = uploadPath + File.separator + "profile" + File.separator + folderPath + File.separator + uuid + "_" + fileName;
      Path savePath = Paths.get(saveName);

      profileImage.transferTo(savePath);

      String dbPath = "/profile/" + folderPath.replace(File.separator, "/") + "/" + uuid + "_" + fileName;
      registerUserDTO.setProfileImg(dbPath);
    }

    registerUserDTO.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
    User user = userRepository.save(dtoToEntity(registerUserDTO));

    System.out.println("회원가입 service 진입");
    System.out.println("email = " + registerUserDTO.getEmail());

    User savedUser = userRepository.save(user);

    System.out.println("저장된 uid = " + savedUser.getUid());

    return user.getUid();
  }

  private String makeFolder(String uploadPath) {
    String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    String folderPath = str.replace("/", File.separator);   // '/'를 운영체제에 맞게 파일 구분자로 변경
    File uploadPathFolder = new File(uploadPath + File.separator + "profile", folderPath);
    if (!uploadPathFolder.exists()) {
      uploadPathFolder.mkdirs();
    }
    return folderPath;
  }

  public ResponseUserDTO login(LoginDTO loginDTO) {
    Optional<User> user = userRepository.findByEmail(loginDTO.getEmail());

    return user
        .filter(value -> passwordEncoder.matches(loginDTO.getPassword(), value.getPassword()))
        .map(this::entityToDto)
        .orElse(null);
  }

  @Override
  public ResponseUserDTO modifyUser(RegisterUserDTO registerUserDTO, MultipartFile profileFile, String uploadPath) throws IOException {

    User user = userRepository.findById(registerUserDTO.getUid())
    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

      // 이름 수정
      if (registerUserDTO.getName() != null && !registerUserDTO.getName().isBlank()) {
        user.changeName(registerUserDTO.getName());
      }

      // 비밀번호 수정
      if (registerUserDTO.getPassword() != null && !registerUserDTO.getPassword().isBlank()) {
        user.changePassword(passwordEncoder.encode(registerUserDTO.getPassword()));
      }

      user.changeLocation(registerUserDTO.getLocation());
      user.changeCategory(registerUserDTO.getCategory());

      // 프로필 이미지 수정
      if (profileFile != null && !profileFile.isEmpty()) {
        if(!profileFile.getContentType().startsWith("image")) {
          throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        String originalName = profileFile.getOriginalFilename();
        String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

        String folderPath = makeFolder(uploadPath);
        String uuid = UUID.randomUUID().toString();

        String saveName= uploadPath + File.separator + "profile" + File.separator + folderPath + File.separator + uuid + "_" + fileName;

        Path savePath = Paths.get(saveName);
        profileFile.transferTo(savePath);

        String dbPath = "/profile/" + folderPath.replace(File.separator, "/") + "/" + uuid + "_" + fileName;

        user.changeProfileImg(dbPath);
      }

      User savedUser = userRepository.save(user);

      return entityToDto(savedUser);
  }
}
