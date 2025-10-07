package com.backoven.catdogshelter.domain.volunteer.command.application.dto;

import lombok.Data;

@Data
public class VolunteerAssociationApproveRequest {
    private Integer applicationId; // 승인할 신청 PK
    private Integer time;          // 승인 시 인정 시간(분). null이면 0 유지
}
