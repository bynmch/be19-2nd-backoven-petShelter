package com.backoven.catdogshelter.domain.volunteer.query.service;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostDetailDTO;
import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostFileDTO;
import com.backoven.catdogshelter.domain.volunteer.query.mapper.VolunteerPostDetailQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolunteerPostDetailServiceImpl implements VolunteerPostDetailService {

    private final VolunteerPostDetailQueryMapper volunteerPostDetailQueryMapper;

    @Transactional
    @Override
    public VolunteerPostDetailDTO getDetail(Integer id, boolean increaseView) {
        if (increaseView) {
            volunteerPostDetailQueryMapper.increaseView(id);
        }

        var detail = volunteerPostDetailQueryMapper.findDetailById(id);
        if (detail == null) {
            throw new IllegalArgumentException("봉사후기 게시글을 찾을 수 없습니다. id=" + id);
        }

        List<VolunteerPostFileDTO> files = volunteerPostDetailQueryMapper.findFilesByPostId(id);
        detail.setFiles(files);
        return detail;
    }
}
