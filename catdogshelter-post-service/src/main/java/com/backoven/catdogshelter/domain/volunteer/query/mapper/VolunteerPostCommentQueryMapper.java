package com.backoven.catdogshelter.domain.volunteer.query.mapper;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostCommentQueryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VolunteerPostCommentQueryMapper {

    List<VolunteerPostCommentQueryDto> selectVolunteerPostCommentsByPostId(Integer postId);
}
