package com.backoven.catdogshelter.domain.volunteer.query.service;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostCommentQueryDto;

import java.util.List;

public interface VolunteerPostCommentQueryService {
    List<VolunteerPostCommentQueryDto> selectVolunteerPostCommetsByPostId(Integer postId);
}
