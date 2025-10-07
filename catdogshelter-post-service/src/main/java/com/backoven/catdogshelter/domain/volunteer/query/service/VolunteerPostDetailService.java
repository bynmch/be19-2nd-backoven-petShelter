package com.backoven.catdogshelter.domain.volunteer.query.service;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostDetailDTO;

public interface VolunteerPostDetailService {
    /**
     * 상세 조회
     * @param id             게시글 ID
     * @param increaseView   true면 조회수 +1
     */
    VolunteerPostDetailDTO getDetail(Integer id, boolean increaseView);
}
