package com.backoven.catdogshelter.domain.volunteer.query.mapper;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostListItemDTO;
import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostSearchCond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VolunteerPostQueryMapper {
    List<VolunteerPostListItemDTO> listByOrder(@Param("cond") VolunteerPostSearchCond cond);
    long countAll();

    List<VolunteerPostListItemDTO> searchByOrder(@Param("cond") VolunteerPostSearchCond cond);
    long countSearch(@Param("cond") VolunteerPostSearchCond cond);
}
