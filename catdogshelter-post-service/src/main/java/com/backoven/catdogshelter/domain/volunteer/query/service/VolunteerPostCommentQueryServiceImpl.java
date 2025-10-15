package com.backoven.catdogshelter.domain.volunteer.query.service;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostCommentQueryDto;
import com.backoven.catdogshelter.domain.volunteer.query.mapper.VolunteerPostCommentQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerPostCommentQueryServiceImpl implements VolunteerPostCommentQueryService{

    private final VolunteerPostCommentQueryMapper commentMapper;

    @Autowired
    public VolunteerPostCommentQueryServiceImpl(VolunteerPostCommentQueryMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public List<VolunteerPostCommentQueryDto> selectVolunteerPostCommetsByPostId(Integer postId) {

        List<VolunteerPostCommentQueryDto> comments = commentMapper.selectVolunteerPostCommentsByPostId(postId);

        return comments;
    }
}
