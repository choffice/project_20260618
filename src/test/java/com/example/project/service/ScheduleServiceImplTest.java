// package com.example.project.service;

// import com.example.project.dto.PageRequestDTO;
// import com.example.project.dto.PageResultDTO;
// import com.example.project.dto.ResponseScheduleDTO;
// import com.example.project.entity.*;
// import com.example.project.repository.PlannersRepository;
// import com.example.project.repository.PlannersUserRepository;
// import com.example.project.repository.ScheduleRepository;
// import com.example.project.repository.UserRepository;
// import com.example.project.security.dto.AuthUserDTO;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.test.annotation.Commit;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import tools.jackson.databind.DatabindException;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.Date;
// import java.util.List;
// import java.util.stream.IntStream;

// import static com.example.project.entity.ScheduleStatus.SCHEDULED;
// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// class ScheduleServiceImplTest {
//   @Autowired
//   private ScheduleRepository scheduleRepository;
//   @Autowired
//   private ScheduleService scheduleService;
//   @Autowired
//   private PlannersRepository plannersRepository;
//   @Autowired
//   private PlannersUserRepository plannersUserRepository;
//   @Autowired
//   private UserRepository userRepository;

//   @Test
//   @Transactional
//   public void list() {
//     //Long tid, Model model, PageRequestDTO pageRequestDTO
// //    List<Schedule> result = scheduleRepository.findByPlanners_Tid(1l);
// //    System.out.println(result);
// //    PageResultDTO<ResponseScheduleDTO, Schedule> result = scheduleService. getScheduleList(pageRequestDTO);
// //    List<ResponseScheduleDTO> responseScheduleList = result.getDtoList();
// //    System.out.println(responseScheduleList);
//   }

//   // @Test
//   // @Transactional
//   // @Commit
//   // public void create() {
//   //   //플래너즈 전체 불러오기
//   //   List<Planners> planners = plannersRepository.findAll();
//   //   //플래너즈를 하나하나 불러올 건데
//   //   IntStream.range(0, planners.size()).forEach(i -> {
//   //     Planners planners1 = planners.get(i);
//   //     //그 플래너즈 안에서 랜덤 유저를 크리에이터로 지정
//   //     List<PlannersUser> users = plannersUserRepository.findByPlanners_Tid(planners1.getTid());
//   //     int randDate = (int) (Math.random()*11)+20;
//   //     int randDays = (int) (Math.random()*3)+3;
//   //     Schedule schedule = Schedule.builder()
//   //         .title("Plan..."+i)
//   //         .description("Content..."+i)
//   //         .startDate(LocalDateTime.now().plusDays(randDate))
//   //         .endDate(LocalDateTime.now().plusDays(randDate+randDays))
//   //         .planners(planners1)
//   //         .creator(users.get((int) (Math.random()*users.size())).getUser())
//   //         .scheduleStatus(ScheduleStatus.SCHEDULED)
//   //         .build();
//   //     scheduleRepository.save(schedule);
//   //   });
//   // }
// }