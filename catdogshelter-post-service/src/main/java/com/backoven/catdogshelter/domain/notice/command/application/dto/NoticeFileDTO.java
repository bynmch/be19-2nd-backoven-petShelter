package com.backoven.catdogshelter.domain.notice.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFileDTO {
    private Integer id;
    private String fileRename;
    private String filePath;
    private String uploadedAt;
}
