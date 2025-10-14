package com.backoven.catdogshelter.domain.volunteer.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerPostFileDTO {
    private Integer id;
    private String fileRename;
    private String filePath;
    private String uploadedAt;
}
