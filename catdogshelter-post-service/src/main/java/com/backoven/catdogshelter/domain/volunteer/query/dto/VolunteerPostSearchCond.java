package com.backoven.catdogshelter.domain.volunteer.query.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VolunteerPostSearchCond {
    // 검색
    private String keyword;  // 제목/내용/작성자 통합 검색 키워드
    private String title;    // 옵션: 제목만
    private String content;  // 옵션: 내용만
    private String author;   // 옵션: 작성자만 (notice는 무시)

    // 정렬
    private String order;    // created | views | likes (notice는 views 미지원 → created로 대체)

    // 페이지
    private Integer limit;   // size
    private Integer offset;  // (page-1)*size
}
