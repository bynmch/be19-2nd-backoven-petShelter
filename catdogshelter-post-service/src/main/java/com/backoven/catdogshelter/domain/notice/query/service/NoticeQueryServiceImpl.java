package com.backoven.catdogshelter.domain.notice.query.service;

import com.backoven.catdogshelter.domain.notice.query.dto.NoticeDetailDTO;
import com.backoven.catdogshelter.domain.notice.query.dto.NoticeFileDTO;
import com.backoven.catdogshelter.domain.notice.query.dto.NoticeListItemDTO;
import com.backoven.catdogshelter.domain.notice.query.dto.NoticeSearchCond;
import com.backoven.catdogshelter.domain.notice.query.mapper.NoticeQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeQueryServiceImpl implements NoticeQueryService {

    private final NoticeQueryMapper mapper;

    // 게시글 상세 조회
    @Override
    public NoticeDetailDTO selectNoticeDetail(Integer id) {
        NoticeDetailDTO notice = mapper.findNoticeById(id);
        if (notice == null) return null;

        List<NoticeFileDTO> files = mapper.findFilesByNoticeId(id);
        notice.setFiles(files);
        return notice;
    }

    // 게시글 목록 조회 (검색 및 정렬 조회)
    @Override
    public Map<String, Object> selectNoticeListBySearchAndOrderBy(
            String keyword,
            String createdFrom,
            String createdTo,
            Integer page,
            Integer size,
            String orderBy,
            String orderDir
    ) {
        // 기본값
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int offset = (p - 1) * s;

        // orderBy 화이트리스트
        String safeOrderBy = "createdAt";
        if ("likeCount".equalsIgnoreCase(orderBy)) safeOrderBy = "likeCount";

        // orderDir 화이트리스트
        String safeOrderDir = "DESC";
        if ("ASC".equalsIgnoreCase(orderDir)) safeOrderDir = "ASC";

        NoticeSearchCond cond = NoticeSearchCond.builder()
                .keyword(keyword)
                .createdFrom(createdFrom)
                .createdTo(createdTo)
                .orderBy(safeOrderBy)
                .orderDir(safeOrderDir)
                .limit(s)
                .offset(offset)
                .build();

        List<NoticeListItemDTO> items = mapper.searchNotices(cond);
        long total = mapper.countNotices(cond);

        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("page", p);
        res.put("size", s);
        res.put("items", items);
        return res;
    }
}
