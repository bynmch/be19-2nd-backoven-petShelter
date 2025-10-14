package com.backoven.catdogshelter.domain.notice.query.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeDetailDTO {
    private Integer id;          // notice.id
    private String title;        // notice.title
    private String content;      // notice.content
    private String createdAt;    // notice.created_at
    private String updatedAt;    // notice.updated_at
    private Integer ratingId;    // notice.rating_id
    private Integer likeCount;      // 공지 좋아요 수 (noticeLiked 카운트)

    // 첨부파일들
    private List<NoticeFileDTO> files;
}
