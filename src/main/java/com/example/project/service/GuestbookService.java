package com.example.project.service;

import com.example.project.dto.GuestbookDTO;
import com.example.project.dto.PageRequestDTO;
import com.example.project.dto.PageResultDTO;
import com.example.project.entity.Guestbook;
import com.example.project.entity.Planners;
import com.example.project.entity.User;

public interface GuestbookService {

  Long register(GuestbookDTO dto);

  PageResultDTO<GuestbookDTO, Guestbook> getList(Long tid, PageRequestDTO pageRequestDTO);

  void modify(GuestbookDTO dto);

  void remove(Long gid);

  default GuestbookDTO entityToDto(Guestbook guestbook) {
    return GuestbookDTO.builder()
        .gid(guestbook.getGid())
        .tid(guestbook.getPlanners().getTid())
        .writerUid(guestbook.getWriter().getUid())
        .writerName(guestbook.getWriter().getName())
        .writerProfileImg(guestbook.getWriter().getProfileImg())
        .content(guestbook.getContent())
        .regDate(guestbook.getRegDate())
        .modDate(guestbook.getModDate())
        .build();
  }

  default Guestbook dtoToEntity(GuestbookDTO dto) {
    return Guestbook.builder()
        .gid(dto.getGid())
        .planners(Planners.builder().tid(dto.getTid()).build())
        .writer(User.builder().uid(dto.getWriterUid()).build())
        .content(dto.getContent())
        .build();
  }
}
