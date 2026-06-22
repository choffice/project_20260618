package com.example.project.service;

import com.example.project.dto.GuestbookDTO;
import com.example.project.dto.PageRequestDTO;
import com.example.project.dto.PageResultDTO;
import com.example.project.entity.Guestbook;
import com.example.project.repository.GuestbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService {

  private final GuestbookRepository guestbookRepository;

  @Override
  public Long register(GuestbookDTO dto) {
    log.info("DTO------------------------");
    log.info(dto);

    Guestbook entity = dtoToEntity(dto);
    log.info(entity);

    guestbookRepository.save(entity);
    return entity.getGid();
  }

  @Override
  public PageResultDTO<GuestbookDTO, Guestbook> getList(Long tid, PageRequestDTO pageRequestDTO) {
    Page<Guestbook> result = guestbookRepository.findByPlanners_Tid(
        tid,
        pageRequestDTO.getPageable(Sort.by("gid").descending()));

    Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity));
    return new PageResultDTO<>(result, fn);
  }

  @Override
  public void modify(GuestbookDTO dto) {
    // 기존 방명록 찾기
    java.util.Optional<Guestbook> result = guestbookRepository.findById(dto.getGid());
    if (result.isPresent()) {
      Guestbook entity = result.get();
      // 내용만 변경
      entity.changeContent(dto.getContent());
      guestbookRepository.save(entity);
    }
  }

  @Override
  public void remove(Long gid) {
    // 삭제 실행
    guestbookRepository.deleteById(gid);
  }
}
