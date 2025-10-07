package com.backoven.catdogshelter.domain.volunteer.query.controller;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerApplicationDTO;
import com.backoven.catdogshelter.domain.volunteer.query.service.VolunteerApplicationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "봉사모임 API")
@RestController
@RequestMapping("/association-posts")
public class VolunteerApplicationQueryController {

    private final VolunteerApplicationQueryService volunteerApplicationQueryService;

    @Autowired
    public VolunteerApplicationQueryController(
            VolunteerApplicationQueryService volunteerApplicationQueryService) {
        this.volunteerApplicationQueryService = volunteerApplicationQueryService;
    }

    /** 단건 조회 (신청 id 기준) */
    @Operation(summary = "신청 내역 조회", description = "신청 내역을 조회할 수 있다.")
    @GetMapping("/application-details/{id}")
    public VolunteerApplicationDTO getOne(@PathVariable Integer id) {
        return volunteerApplicationQueryService.getById(id);
    }

    /** 특정 봉사모임(volunteerAssociation)의 신청자 목록 */
    @Operation(summary = "신청 내역 목록 조회", description = "봉사모임번호를 기준으로 신청 내역 목록을 조회할 수 있다.")
    @GetMapping("/{volunteerId}/application-list")
    public List<VolunteerApplicationDTO> getListByVolunteer(
            @PathVariable Integer volunteerId,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit
    ) {
        return volunteerApplicationQueryService.getListByVolunteerId(volunteerId, offset, limit);
    }
}
