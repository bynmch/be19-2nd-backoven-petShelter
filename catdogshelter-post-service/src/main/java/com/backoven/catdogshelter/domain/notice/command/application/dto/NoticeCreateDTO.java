package com.backoven.catdogshelter.domain.notice.command.application.dto;

import lombok.*;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreateDTO {
    private String title;
    private String content;
}
