package com.backoven.catdogshelter.domain.notice.query.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeListItemDTO {
    private Integer id;
    private String title;
    private String content;      // 목록에서는 미리보기 용도(앞부분만)
    private String createdAt;
    private String updatedAt;
    private Integer likeCount;   // LEFT JOIN 집계
}
