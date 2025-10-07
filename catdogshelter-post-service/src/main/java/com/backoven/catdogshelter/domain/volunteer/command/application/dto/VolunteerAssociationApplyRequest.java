package com.backoven.catdogshelter.domain.volunteer.command.application.dto;

import lombok.Data;

@Data
public class VolunteerAssociationApplyRequest {
    private Integer volunteerId; // 대상 모임
    private Integer userId;      // 신청자
}
