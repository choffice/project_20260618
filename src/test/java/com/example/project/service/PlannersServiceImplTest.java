// package com.example.project.service;

// import com.example.project.entity.Planners;
// import com.example.project.entity.PlannersUser;
// import com.example.project.entity.User;
// import com.example.project.repository.PlannersRepository;
// import com.example.project.repository.PlannersUserRepository;
// import com.example.project.repository.UserRepository;
// import jakarta.transaction.Transactional;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.core.parameters.P;
// import org.springframework.test.annotation.Commit;

// import java.lang.reflect.Array;
// import java.util.*;
// import java.util.stream.Collectors;
// import java.util.stream.IntStream;

// import static com.example.project.entity.PlannersRole.USER;
// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// class PlannersServiceImplTest {
//   @Autowired
//   private PlannersUserRepository plannersUserRepository;

//   @Autowired
//   private UserRepository userRepository;

//   @Autowired
//   private PlannersRepository plannersRepository;

// @Test
//   public void getMemberListTest() {
//     List<PlannersUser> result = plannersUserRepository.getMemberList(1l);
//     List<User> user = result.stream().map(r -> r.getUser()).toList();
//     System.out.println(user);
//   }
// //   @Test
// //   @Transactional
// //   public void getPlannersUserByTid_Test() {
// //     Long tid = 1l;
// //     List<PlannersUser> result = plannersUserRepository.findByPlanners_Tid(tid);
// // //    result.get(0).getMid();
// //     User o = result.get(0).getPlanners().getOwner();
// //     Map<String, List<Object>> members = new HashMap<>();
// //     //방장 추출
// //     members.put("owner", Arrays.asList(o.getUid(), o.getName(), o.getProfileImg()));
// //     //멤버 추출
// //     List<Object> notowner = new ArrayList<>();
// //     result.forEach(m -> {
// //       Object[] arr = {m.getUser().getUid(), m.getUser().getName(), m.getUser().getProfileImg()};
// //       notowner.add(arr);
// //     });
// //     members.put("members", notowner);

// //     if(!result.isEmpty()) System.out.println(members);
// //     else System.out.println("결과없음");
// // }

// // ;

// // @Test
// // @Commit
// // public void plannersJoinTest() {
// //   //플래너즈 전체 불러오기
// //   List<Planners> planners = plannersRepository.findAll();
// //   //플래너즈를 하나하나 불러올 건데
// //   IntStream.range(0, planners.size()).forEach(i -> {
// //     //전체 유저를 불러와서
// //     List<User> users = userRepository.findAll();
// // //    System.out.println(users.size());
// //     //해당 그룹에 이미 속한 유저를 제거
// //     List<PlannersUser> pusers = plannersUserRepository.findByPlanners_Tid(planners.get(i).getTid());
// //     users.removeAll(
// //         pusers.stream()
// //             .map(PlannersUser::getUser) // PlannersUser에서 User 꺼내기
// //             .collect(Collectors.toList())
// //     );
// // //    System.out.println(users.size());
// //     PlannersUser user = PlannersUser.builder()
// //         .planners(planners.get(i))
// //         .user(users.get((int) (Math.random()*users.size()-1)+1))
// //         .role(USER)
// //         .build();
// //     plannersUserRepository.save(user);
// //   });
// // }
// }