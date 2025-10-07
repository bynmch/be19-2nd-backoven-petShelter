package com.backoven.catdogshelter.domain.volunteer.command.application.dto;

import lombok.Data;

@Data
public class VolunteerAssociationDTO {
    private String title;
    private String content;
    private Integer time;
    private String startDate;      // "yyyy-MM-dd HH:mm" 등
    private String detailAddress;  // optional
    private Boolean deadline;      // 기본 false
    private Integer numberOfPeople;
    private Integer headId;        // ShelterHeadEntity PK
    private Integer sigunguId;     // SigunguEntity PK
}
