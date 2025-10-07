package com.backoven.catdogshelter.domain.volunteer.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Schema(name = "신청내역DTO")
public class VolunteerApplicationDTO {
    @Schema(name = "신청번호")
    private Integer id;          // 신청내역 PK
    @Schema(name = "신청승인여부")
    private Boolean status;      // 승인 여부
    @Schema(name = "봉사모임게시글번호")
    private Integer volunteerId; // volunteerAssociation.id
    @Schema(name = "신청일반회원아이디")
    private Integer userId;      // user.user_id
    @Schema(name = "신청일반회원이름")
    private String userName;     // user.user_name
}
