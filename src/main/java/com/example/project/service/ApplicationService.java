package com.example.project.service;


import com.example.project.dto.ApplicationDTO;

import java.util.List;

public interface ApplicationService {

  // 1. 가입 양식 불러오기
  String getForm(Long tid);

  // 2. 가입 양식 저장
  void saveForm(Long tid, ApplicationDTO.FormSaveRequest request) throws Exception;

  // 3. 가입 신청
  Long submitApplication(Long tid, ApplicationDTO.SubmitRequest request, Long uid) throws Exception;

  // 4. 가입 신청 목록 불러오기
  List<ApplicationDTO.ApplicationResponse> getApplications (Long tid);

  // 5. 가입 신청 승인
  void approveApplication (Long applicationId);

  // 6. 가입 신청 거절
  void rejectApplication (Long applicationId);


}
