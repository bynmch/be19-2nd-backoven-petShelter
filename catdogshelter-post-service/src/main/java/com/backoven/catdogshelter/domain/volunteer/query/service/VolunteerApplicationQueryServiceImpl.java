package com.backoven.catdogshelter.domain.volunteer.query.service;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerApplicationDTO;
import com.backoven.catdogshelter.domain.volunteer.query.mapper.VolunteerApplicationQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolunteerApplicationQueryServiceImpl implements VolunteerApplicationQueryService {

    private final VolunteerApplicationQueryMapper mapper;

    @Override
    public VolunteerApplicationDTO getById(Integer id) {
        var dto = mapper.findById(id);
        if (dto == null) {
            throw new IllegalArgumentException("신청내역을 찾을 수 없습니다. id=" + id);
        }
        return dto;
    }

    @Override
    public List<VolunteerApplicationDTO> getListByVolunteerId(Integer volunteerId, Integer offset, Integer limit) {
        // 기본 페이징 값 (원하면 조정하세요)
        if (limit == null || limit <= 0) limit = 20;
        if (offset == null || offset < 0) offset = 0;
        return mapper.findAllByVolunteerId(volunteerId, offset, limit);
    }
}
