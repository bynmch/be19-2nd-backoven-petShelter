package com.backoven.catdogshelter.domain.notice.query.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeFileDTO {
    private Integer id;          // noticeFiles.id
    private String fileRename;   // noticeFiles.file_rename
    private String filePath;     // noticeFiles.file_path
    private String uploadedAt;   // noticeFiles.uploaded_at
}
