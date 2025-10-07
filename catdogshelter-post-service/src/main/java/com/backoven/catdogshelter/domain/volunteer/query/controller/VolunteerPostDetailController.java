package com.backoven.catdogshelter.domain.volunteer.query.controller;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostDetailDTO;
import com.backoven.catdogshelter.domain.volunteer.query.service.VolunteerPostDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "봉사후기 API")
@RestController
@RequestMapping("/volunteer-posts/post")
public class VolunteerPostDetailController {

    private final VolunteerPostDetailService volunteerPostDetailService;

    @Autowired
    public VolunteerPostDetailController(VolunteerPostDetailService volunteerPostDetailService) {
        this.volunteerPostDetailService = volunteerPostDetailService;
    }

    //
    // - inc=true(default) : 조회수 +1
    // - inc=false         : 조회수 증가 없이 조회
    @Operation(summary = "게시판 상세조회",
            description = "{id}번의 게시글을 상세조회하여 내용, 댓글과 이미지를 보여줄 수 있다.")
    @GetMapping("/{id}")

    public VolunteerPostDetailDTO getOne(@PathVariable Integer id,
                                         @RequestParam(name = "inc", required = false, defaultValue = "true") boolean inc) {
        return volunteerPostDetailService.getDetail(id, inc);
    }
}
