// package com.example.project.repository;

// import com.example.project.entity.*;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.util.stream.LongStream;

// @SpringBootTest
// class PlannersRepositoryTests {
//   @Autowired
//   private PlannersRepository plannersRepository;

//   @Autowired
//   private PlannersUserRepository plannersUserRepository;

//   @Test
//   public void insertPlanners() {
//     Planners planners = Planners.builder()
//         .name("Test Planner")
//         .description("Test Planner")
//         .location("서울")
//         .category("취미")
//         .maxPopulation(20)
//         .population(0)
//         .owner(User.builder().uid(1L).build())
//         .status(PlannersStatus.PUBLIC)
//         .build();
//     plannersRepository.save(planners);
//   }

//   @Test
//   public void insertPlannersUser() {
//     LongStream.rangeClosed(20L, 100L).forEach(i -> {
//       PlannersUser plannersUser = PlannersUser.builder()
//           .planners(Planners.builder().tid(5L).build())
//           .user(User.builder().uid(i).build())
//           .role(PlannersRole.USER)
//           .build();
//       plannersUserRepository.save(plannersUser);
//     });
//   }
// }