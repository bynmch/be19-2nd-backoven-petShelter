package com.backoven.catdogshelter.domain.notice.query.mapper;

import com.backoven.catdogshelter.domain.notice.query.dto.NoticeDetailDTO;
import com.backoven.catdogshelter.domain.notice.query.dto.NoticeFileDTO;
import com.backoven.catdogshelter.domain.notice.query.dto.NoticeListItemDTO;
import com.backoven.catdogshelter.domain.notice.query.dto.NoticeSearchCond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeQueryMapper {

    // 게시글 상세 조회
    NoticeDetailDTO findNoticeById(@Param("id") Integer id);

    // 게시글 파일
    List<NoticeFileDTO> findFilesByNoticeId(@Param("noticeId") Integer noticeId);

    // 게시글 목록 조회
    List<NoticeListItemDTO> searchNotices(@Param("cond") NoticeSearchCond cond);

    long countNotices(@Param("cond") NoticeSearchCond cond);
}
