package com.backoven.catdogshelter.domain.volunteer.query.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VolunteerPostListItemDTO {
    private Integer id;
    private String title;
    private String contentPreview; // 일부 미리보기
    private String createdAt;
    private Integer views;         // notice는 null
    private Integer likeCount;
    private String author;         // notice는 null (스키마에 따라 확장)
}
