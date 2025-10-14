package com.backoven.catdogshelter.domain.notice.query.service;

import com.backoven.catdogshelter.domain.notice.query.dto.NoticeDetailDTO;

import java.util.Map;

public interface NoticeQueryService {

    // 게시글 상세 조회
    NoticeDetailDTO selectNoticeDetail(Integer id);

    // 게시글 목록 조회(검색, 정렬, 페이지 조회)
    Map<String, Object> selectNoticeListBySearchAndOrderBy(
            String keyword,
            String createdFrom,
            String createdTo,
            Integer page,        // 1부터
            Integer size,        // 페이지 크기
            String orderBy,      // createdAt | likeCount
            String orderDir      // ASC | DESC
    );
}
