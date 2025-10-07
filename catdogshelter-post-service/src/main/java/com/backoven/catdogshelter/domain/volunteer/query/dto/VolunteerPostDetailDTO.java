package com.backoven.catdogshelter.domain.volunteer.query.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VolunteerPostDetailDTO {
    private Integer id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Integer views;
    private Boolean isBlinded;
    private Boolean isDeleted;

    private Integer likeCount;   // 좋아요 수
    private String author;       // 작성자 (u.user_name 또는 h.company_name/ceo_name)

    private List<VolunteerPostFileDTO> files; // 첨부파일 목록
}
