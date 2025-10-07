package com.backoven.catdogshelter.domain.volunteer.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "수정할 봉사모임게시글 정보DTO")
public class VolunteerAssociationUpdateDTO {
    @Schema(name = "게시글 제목")
    private String title;
    @Schema(name = "게시글 내용")
    private String content;
    @Schema(name = "봉사시간")
    private Integer time;
    @Schema(name = "모임일자")
    private String startDate;
    @Schema(name = "상세주소")
    private String detailAddress;
    @Schema(name = "모집마감여부")
    private Boolean deadline;
    @Schema(name = "모집인원")
    private Integer numberOfPeople;
    @Schema(name = "모임종료여부")
    private Boolean isEnd; // 모임 종료 처리도 수정에서 허용
}
