package com.backoven.catdogshelter.domain.notice.command.application.controller;

import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeCreateDTO;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeLikeToggleRequest;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeUpdateDTO;
import com.backoven.catdogshelter.domain.notice.command.application.service.NoticeService;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "공지사항 API")
@RestController
@RequestMapping("/notice-posts")
public class NoticeCommandController {

    private final NoticeService noticeService;
    private final ObjectMapper om;

    @Autowired
    public NoticeCommandController(NoticeService noticeService,
                                   ObjectMapper om) {
        this.noticeService = noticeService;
        this.om = om;
    }

    // 게시글 등록
    @Operation(summary = "게시글 등록", description = "관리자는 파일과 함께 게시글을 등록할 수 있다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> writeNotice(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception{
        NoticeCreateDTO dto = om.readValue(dtoJson, NoticeCreateDTO.class);
        Integer id = noticeService.writeNotice(dto, files == null ? List.of() : files);
        return ResponseEntity.ok(Map.of("noticeId", id));
    }

    // 게시글 수정
    @Operation(summary = "게시글 수정", description = "관리자는 파일과 함께 게시글을 수정할 수 있다.")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> modifyNotice(
            @PathVariable Integer id,
            @RequestPart("dto")  String dtoJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> newFiles
    ) throws Exception {
        NoticeUpdateDTO dto = om.readValue(dtoJson, NoticeUpdateDTO.class);
        noticeService.modifyNotice(id, dto, newFiles == null ? List.of() : newFiles);
        return ResponseEntity.noContent().build();
    }

    // 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "관리자는 파일과 함께 게시글을 삭제할 수 있다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable Integer id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    // 게시글 추천
    @Operation(summary = "게시글 추천",
            description = "게시글 이용자는 게시글을 추천하거나 취소 할 수 있다.")
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Integer id,
            @RequestBody NoticeLikeToggleRequest request
    ) {
        boolean liked = noticeService.toggleLike(id, request);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
