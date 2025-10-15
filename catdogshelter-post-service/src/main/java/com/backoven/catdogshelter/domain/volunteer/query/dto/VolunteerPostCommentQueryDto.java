package com.backoven.catdogshelter.domain.volunteer.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerPostCommentQueryDto {
    private Integer commentId;
    private String content;
    private String createdAt;
    private String writerName;
}
