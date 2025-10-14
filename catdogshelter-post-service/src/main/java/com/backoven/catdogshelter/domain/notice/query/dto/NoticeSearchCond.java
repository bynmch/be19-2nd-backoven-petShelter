package com.backoven.catdogshelter.domain.notice.query.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeSearchCond {
    // 텍스트 검색
    private String keyword;          // 제목/내용 LIKE

    // 날짜 범위 (created_at이 VARCHAR(20)이라 가정: 'YYYY-MM-DD' 형태)
    private String createdFrom;      // >=
    private String createdTo;        // <=

    // 정렬/페이징
    private String orderBy;          // createdAt | likeCount (화이트리스트만 허용)
    private String orderDir;         // ASC | DESC
    private Integer limit;           // 페이지 크기
    private Integer offset;          // 시작 인덱스
}
