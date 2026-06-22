package com.example.project.service;

import com.example.project.dto.PageRequestDTO;
import com.example.project.dto.PageResultDTO;
import com.example.project.dto.RegisterPlannersDTO;
import com.example.project.dto.ResponsePlannersDTO;
import com.example.project.dto.ResponseUserDTO;
import com.example.project.entity.Planners;
import com.example.project.entity.PlannersUser;
import com.example.project.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PlannersService {

  PageResultDTO<ResponsePlannersDTO, Planners> getPlannersList(PageRequestDTO pageRequestDTO, Long uid);

  PageResultDTO<ResponsePlannersDTO, Planners> getMyPlannersList(PageRequestDTO pageRequestDTO, Long uid);

  PageResultDTO<ResponsePlannersDTO, Planners> getInvitedPlannersList(PageRequestDTO pageRequestDTO, Long uid);

  PageResultDTO<ResponsePlannersDTO, Planners> getCreatedPlannersList(PageRequestDTO pageRequestDTO, Long uid);

  PageResultDTO<ResponsePlannersDTO, Planners> getFavoritePlannersList(PageRequestDTO pageRequestDTO, Long uid);

  Long createPlanners(RegisterPlannersDTO registerPlannersDTO, Long uid,
                      MultipartFile plannersThumbnail, MultipartFile plannersBanner,
                      String uploadPath) throws IOException;

  Long modifyPlanners(RegisterPlannersDTO registerPlannersDTO, Long uid,
                      MultipartFile plannersThumbnail, MultipartFile plannersBanner,
                      String uploadPath) throws IOException;

  void deletePlanners(Long tid);

  List<ResponsePlannersDTO> getPopularPlanners(Long uid);

  List<ResponsePlannersDTO> getRecommendedPlanners(Long uid);

  ResponsePlannersDTO getPlannersById(Long tid);

  List<ResponseUserDTO> getPreviewMembers(Long tid);

  int getMemberCount(Long tid);

  ResponseUserDTO getOwner(Long tid);

  List<ResponseUserDTO> getMemberList(Long tid);

  // Map<String, List<Object>> getPlannersUserByTid(Long tid);
  Map<String, Object> getPlannersUserByTid(Long tid, int memberPage);

  Long inviteUser(Long uid, Long tid, String email);

  Long acceptInvitation(Long uid, Long tid);

  Long declineInvitation(Long uid, Long tid);

    //오창 20260618 멤버 삭제 (추방 탈퇴용)
  Long deleteUserFromPlanners(Long tid, Long uid);

  List<ResponsePlannersDTO> getMyPlanners(Long uid);

  boolean isMember(Long tid, Long uid);

  default ResponsePlannersDTO entityToDto(Planners planners) {
    ResponsePlannersDTO responsePlannersDTO = ResponsePlannersDTO.builder()
        .tid(planners.getTid())
        .name(planners.getName())
        .description(planners.getDescription())
        .adminEmail(planners.getOwner().getEmail())
        .maxPopulation(planners.getMaxPopulation())
        .population(planners.getPopulation())
        .plannersThumbnail(planners.getPlannersThumbnail())
        .plannersBanner(planners.getPlannersBanner())
        .status(planners.getStatus())
        .location(planners.getLocation())
        .category(planners.getCategory())
        .regDate(planners.getRegDate())
        .modDate(planners.getModDate())
        .build();

    return responsePlannersDTO;
  }

  default Planners dtoToEntity(RegisterPlannersDTO registerPlannersDTO) {
    Planners planners = Planners.builder()
        .name(registerPlannersDTO.getName())
        .description(registerPlannersDTO.getDescription())
        .maxPopulation(registerPlannersDTO.getMaxPopulation())
        .population(1)
        .plannersThumbnail(registerPlannersDTO.getPlannersThumbnail())
        .plannersBanner(registerPlannersDTO.getPlannersBanner())
        .location(registerPlannersDTO.getLocation())
        .category(registerPlannersDTO.getCategory())
        .status(registerPlannersDTO.getStatus())
        .build();
    return planners;
  }
}
