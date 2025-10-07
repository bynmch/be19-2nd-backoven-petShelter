package com.backoven.catdogshelter.domain.volunteer.query.service;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerApplicationDTO;

import java.util.List;

public interface VolunteerApplicationQueryService {
    VolunteerApplicationDTO getById(Integer id);

    List<VolunteerApplicationDTO> getListByVolunteerId(Integer volunteerId, Integer offset, Integer limit);
}
