package com.backoven.catdogshelter.domain.volunteer.command.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class VolunteerPostUpdateDTO {
    private String title;
    private String content;
    private List<Integer> deleteFileIds = new ArrayList<>();
}