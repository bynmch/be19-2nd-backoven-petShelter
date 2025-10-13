package com.backoven.catdogshelter.domain.notice.command.application.service;

import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeCreateDTO;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeLikeToggleRequest;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoticeService {

    // 게시글 등록
    Integer writeNotice(NoticeCreateDTO dto, List<MultipartFile> files);

    // 게시글 수정
    void modifyNotice(Integer id, NoticeUpdateDTO dto, List<MultipartFile> newFiles);

    // 게시글 삭제
    void deleteNotice(Integer id);

    // 게시글 추천
    boolean toggleLike(Integer noticeId, NoticeLikeToggleRequest request);
}
