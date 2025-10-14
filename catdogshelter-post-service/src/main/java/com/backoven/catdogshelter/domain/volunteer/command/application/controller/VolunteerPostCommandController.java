package com.backoven.catdogshelter.domain.volunteer.command.application.controller;

import com.backoven.catdogshelter.domain.volunteer.command.application.dto.*;
import com.backoven.catdogshelter.domain.volunteer.command.application.service.VolunteerPostService;
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

@Tag(name = "봉사후기 API")
@RestController
@RequestMapping("/volunteer-posts")
public class VolunteerPostCommandController {

    private final VolunteerPostService volunteerPostService;
    private final ObjectMapper om;

    @Autowired
    public VolunteerPostCommandController(VolunteerPostService volunteerPostService, ObjectMapper om) {
        this.volunteerPostService = volunteerPostService;
        this.om = om;
    }

    // 게시글 등록
    @Operation(summary = "게시글 등록",
            description = "게시글 이용자는 사진파일과 함께 게시글을 작성할 수 있다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> writeVolunteerPost(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        var dto = om.readValue(dtoJson, VolunteerPostCreateDTO.class);
        Integer id = volunteerPostService.writeVolunteerPost(dto, files == null ? List.of() : files);
        return ResponseEntity.ok(Map.of("postId", id));
    }

    // 게시글 수정
    @Operation(summary = "게시글 수정",
            description = "게시글 이용자는 자신이 작성한 게시글을 사진파일과 함께 수정할 수 있다.")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> modifyVolunteerPost(
            @PathVariable Integer id,
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> newFiles
    ) throws Exception {
        VolunteerPostUpdateDTO dto = om.readValue(dtoJson, VolunteerPostUpdateDTO.class);
        volunteerPostService.modifyVolunteerPost(id, dto, newFiles == null ? List.of() : newFiles);
        return ResponseEntity.noContent().build();
    }

    // 게시글 삭제
    @Operation(summary = "게시글 삭제",
            description = "게시글 이용자는 자신이 작성한 게시글을 삭제할 수 있다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteerPost(@PathVariable Integer id) {
        volunteerPostService.deleteVolunteerPost(id);
        return ResponseEntity.noContent().build();
    }

    // 게시글 추천
    @Operation(summary = "게시글 추천",
            description = "게시글 이용자는 게시글을 추천하거나 취소 할 수 있다.")
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Integer id,
            @RequestBody VolunteerPostLikeToggleRequest req
    ) {
        boolean liked = volunteerPostService.toggleLike(id, req);
        return ResponseEntity.ok(Map.of("liked", liked));
    }


    // 게시글 신고
    @Operation(summary = "게시글 신고",
            description = "게시판 이용자는 게시글을 신고할 수 있다. ")
    @PostMapping("/{postId}/report")
    public ResponseEntity<?> reportVolunteerPost(
            @PathVariable Integer postId,
            @RequestBody VolunteerPostReportCreateRequest req
    ) {
        try {
            req.setPostId(postId);
            Integer id = volunteerPostService.reportVolunteerPost(req);
            return ResponseEntity.ok(Map.of("reportId", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 댓글 작성
    @Operation(summary = "댓글 등록",
            description = "게시글 이용자는 댓글을 작성할 수 있다.")
    @PostMapping("/{id}/comment")
    public ResponseEntity<Map<String, Object>> addVolunteerPostComment(
            @PathVariable Integer id,
            @RequestBody VolunteerPostCommentCreateDTO dto
    ) {
        dto.setPostId(id);
        Integer cmtId = volunteerPostService.addVolunteerPostComment(dto);
        return ResponseEntity.ok(Map.of("commentId", cmtId));
    }

    // 댓글 수정
    @Operation(summary = "댓글 수정",
            description = "게시글 이용자는 자신이 작성한 댓글을 수정할 수 있다.")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> modifyVolunteerPostComment(
            @PathVariable Integer commentId,
            @RequestBody VolunteerPostCommentUpdateDTO dto
    ) {
        volunteerPostService.modifyVolunteerPostComment(commentId, dto);
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제
    @Operation(summary = "댓글 삭제",
            description = "게시글 이용자는 자신이 작성한 댓글을 삭제할 수 있다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteVolunteerPostComment(@PathVariable Integer commentId) {
        volunteerPostService.deleteVolunteerPostComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 신고
    @Operation(summary = "댓글 신고",
            description = "게시판 이용자는 댓글을 신고할 수 있다.")
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<?> reportVolunteerPostComment(
            @PathVariable Integer commentId,
            @RequestBody VolunteerPostCommentReportCreateRequest req
    ) {
        try {
            req.setCommentId(commentId);
            Integer id = volunteerPostService.reportVolunteerPostComment(req);
            return ResponseEntity.ok(Map.of("reportId", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
