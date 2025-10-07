package com.backoven.catdogshelter.domain.volunteer.query.mapper;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerApplicationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VolunteerApplicationQueryMapper {

    // 단건 조회 (신청내역 id 기준)
    VolunteerApplicationDTO findById(@Param("id") Integer id);

    // 목록 조회 (특정 volunteerAssociation 에 대한 신청자 목록)
    List<VolunteerApplicationDTO> findAllByVolunteerId(
            @Param("volunteerId") Integer volunteerId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );
}
