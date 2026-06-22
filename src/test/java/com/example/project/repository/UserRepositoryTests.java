// package com.example.project.repository;

// import com.example.project.entity.User;
// import com.example.project.entity.UserRole;
// import com.example.project.entity.UserStatus;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.util.stream.IntStream;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// class UserRepositoryTests {
//   @Autowired
//   private UserRepository userRepository;

//   @Test
//   void insertDummies() {
//     IntStream.rangeClosed(0, 100).forEach(i -> {
//       User user = User.builder()
//           .email("user" + i + "@example.com")
//           .password("1234")
//           .name("user" + i)
//           .status(UserStatus.ACTIVE)
//           .build();
//       user.addMemberRole(UserRole.USER);
//       userRepository.save(user);
//     });
//   }
// }