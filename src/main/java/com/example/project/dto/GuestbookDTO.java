package com.example.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestbookDTO {
  private Long gid;
  
  // 방명록 작성된 플래너즈 그룹
  private Long tid;
  
  // 작성자 정보
  private Long writerUid;
  private String writerName;
  private String writerProfileImg;
  
  // 방명록 내용
  private String content;
  
  // 작성/수정일시
  @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
  private LocalDateTime regDate;
  
  @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
  private LocalDateTime modDate;
}
