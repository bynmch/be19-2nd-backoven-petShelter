package com.backoven.catdogshelter.domain.volunteer.command.application.dto;

import com.backoven.catdogshelter.common.util.ReportCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VolunteerPostCommentReportRequest {
    private Integer commentId;
    private ReportCategory category;
    private String etcDetail;
    private VolunteerPostLikeToggleRequest.ActorType actorType;
    private Integer userId;
    private Integer headId;
}
