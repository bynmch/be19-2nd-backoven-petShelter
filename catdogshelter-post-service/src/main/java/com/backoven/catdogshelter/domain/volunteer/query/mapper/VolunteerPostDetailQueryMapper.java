package com.backoven.catdogshelter.domain.volunteer.query.mapper;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostDetailDTO;
import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostFileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VolunteerPostDetailQueryMapper {

    // 본문 + 메타(작성자/좋아요 수)
    VolunteerPostDetailDTO findDetailById(@Param("id") Integer id);

    // 파일 목록
    List<VolunteerPostFileDTO> findFilesByPostId(@Param("id") Integer id);

    // 조회수 +1
    int increaseView(@Param("id") Integer id);
}
