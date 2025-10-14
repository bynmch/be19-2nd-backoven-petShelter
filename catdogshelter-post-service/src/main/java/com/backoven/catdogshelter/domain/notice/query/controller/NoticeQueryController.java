package com.backoven.catdogshelter.domain.notice.query.controller;

import com.backoven.catdogshelter.domain.notice.query.dto.NoticeDetailDTO;
import com.backoven.catdogshelter.domain.notice.query.service.NoticeQueryService;
import com.backoven.catdogshelter.domain.volunteer.query.service.VolunteerAssociationQueryService;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "공지사항 API")
@RestController
@RequestMapping("/notice-posts")
public class NoticeQueryController {

    private final NoticeQueryService noticeQueryService;
    private final VolunteerAssociationQueryService volunteerAssociationQueryService;

    @Autowired
    public NoticeQueryController(NoticeQueryService noticeQueryService,
                                 VolunteerAssociationQueryService volunteerAssociationQueryService) {
        this.noticeQueryService = noticeQueryService;
        this.volunteerAssociationQueryService = volunteerAssociationQueryService;
    }

    // {id}번 게시글 상세조회
    @Operation(summary = "게시글 상세 조회", description = "게시글과 파일을 조회할 수 있다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> selectNoticeDetail(@PathVariable Integer id) {
        NoticeDetailDTO dto = noticeQueryService.selectNoticeDetail(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

//    // 게시글 목록 조회
//    @Operation(summary = "게시글 목록 조회", description = "게시글의 목록을 조건에 따라 조회할 수 있다.")
//    @GetMapping(value = {"", "/search"})
//    public Map<String, Object> list(
//            @RequestParam(value = "keyword", required = false) String keyword,
//            @RequestParam(value = "createdFrom", required = false) String createdFrom,
//            @RequestParam(value = "createdTo", required = false) String createdTo,
//            @RequestParam(value = "page", required = false) Integer page,
//            @RequestParam(value = "size", required = false) Integer size,
//            @RequestParam(value = "orderBy", required = false) String orderBy,
//            @RequestParam(value = "orderDir", required = false) String orderDir
//    ) {
//        return noticeQueryService.search(keyword, createdFrom, createdTo, page, size, orderBy, orderDir);
//    }
}
