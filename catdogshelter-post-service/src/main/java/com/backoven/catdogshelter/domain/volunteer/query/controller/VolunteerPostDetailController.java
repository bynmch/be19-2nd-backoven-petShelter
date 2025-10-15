package com.backoven.catdogshelter.domain.volunteer.query.controller;

import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostCommentQueryDto;
import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostDetailDTO;
import com.backoven.catdogshelter.domain.volunteer.query.service.VolunteerPostCommentQueryService;
import com.backoven.catdogshelter.domain.volunteer.query.service.VolunteerPostDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "봉사후기 API")
@RestController
@RequestMapping("/volunteer-posts/post")
public class VolunteerPostDetailController {

    private final VolunteerPostDetailService volunteerPostDetailService;
    private final VolunteerPostCommentQueryService volunteerPostCommentQueryService;

    @Autowired
    public VolunteerPostDetailController(VolunteerPostDetailService volunteerPostDetailService, VolunteerPostCommentQueryService volunteerPostCommentQueryService) {
        this.volunteerPostDetailService = volunteerPostDetailService;
        this.volunteerPostCommentQueryService = volunteerPostCommentQueryService;
    }

    // 게시글 상세조회
    @Operation(summary = "게시판 상세조회",
            description = "{id}번의 게시글을 상세조회하여 내용, 댓글과 이미지를 보여줄 수 있다.")
    @GetMapping("/{id}")

    public VolunteerPostDetailDTO getOne(@PathVariable Integer id,
                                         @RequestParam(name = "inc", required = false, defaultValue = "true") boolean inc) {
        return volunteerPostDetailService.getDetail(id, inc);
    }

    // 댓글 조회({postId}에 해당하는 댓글 모두 조회)
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<VolunteerPostCommentQueryDto>> selectVolunteerPostCommentsByPostId(@PathVariable Integer postId) {
        List<VolunteerPostCommentQueryDto> comments = volunteerPostCommentQueryService.selectVolunteerPostCommetsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
}
