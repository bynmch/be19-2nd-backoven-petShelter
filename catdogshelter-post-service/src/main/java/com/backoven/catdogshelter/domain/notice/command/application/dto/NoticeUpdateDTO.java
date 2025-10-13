package com.backoven.catdogshelter.domain.notice.command.application.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class NoticeUpdateDTO {
    private String title;
    private String content;
    private List<Integer> deleteFileIds = new ArrayList<>();
}
