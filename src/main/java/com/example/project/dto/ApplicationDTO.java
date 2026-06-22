package com.example.project.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

public class ApplicationDTO {

  @Data
  public static class FormSaveRequest {
    private List<Map<String, String>> formSchema;
  }

  @Data
  public static class SubmitRequest {
    private List<Map<String, String>> answers;
  }

  @Data
  public static class ApplicationResponse {
    private Long id;
    private String userName;
    private String userEmail;
    private List<Map<String, String>> answers;
    private String status;
  }
}
