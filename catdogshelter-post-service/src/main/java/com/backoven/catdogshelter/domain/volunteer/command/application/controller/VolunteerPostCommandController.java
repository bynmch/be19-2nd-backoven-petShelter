// package: com.backoven.catdogshelter.domain.volunteer.command.application.controller
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

    // 생성: multipart (dto + files[])
    @Operation(summary = "게시글 등록", description = "봉사후기 게시글을 사진파일과 함께 작성할 수 있다.")

    @PostMapping(value = "/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> writeVolunteerPost(
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        var dto = om.readValue(dtoJson, VolunteerPostCreateDTO.class);
        Integer id = volunteerPostService.writeVolunteerPost(dto, files == null ? List.of() : files);
        return ResponseEntity.ok(Map.of("postId", id));
    }

    // 수정: multipart (dto + newFiles[])

    @Operation(summary = "게시글 수정",
            description = "봉사후기 게시글을 사진파일과 함께 수정할 수 있다.")
    @PatchMapping(value = "/{id}/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> modifyVolunteerPost(
            @PathVariable Integer id,
            @RequestPart("dto") String dtoJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> newFiles
    ) throws Exception {
        var dto = om.readValue(dtoJson, VolunteerPostUpdateDTO.class);
        volunteerPostService.modifyVolunteerPost(id, dto, newFiles == null ? List.of() : newFiles);
        return ResponseEntity.noContent().build();
    }

    // 소프트 삭제
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제할 수 있다.")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteVolunteerPost(@PathVariable Integer id) {
        volunteerPostService.deleteVolunteerPost(id);
        return ResponseEntity.noContent().build();
    }

    // 좋아요 토글
    @Operation(summary = "게시글 추천",
            description = "추천을 누르면 내역에 저장되고 " +
            "\n다시 한 번 누르면 내역이 삭제되어 좋아요 수를 " +
                    "\n늘리거나 줄여서 좋아요 수를 카운트한다.")
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Integer id,
            @RequestBody VolunteerPostLikeToggleRequest req
    ) {
        boolean liked = volunteerPostService.toggleLike(id, req);
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    // 게시글 신고
//    @PostMapping("/{id}/report")
//    public ResponseEntity<Void> reportPost(
//            @PathVariable Integer id,
//            @RequestBody VolunteerPostReportRequest req
//    ) {
//        req.setPostId(id);
//        volunteerPostService.reportPost(req);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

    // 봉사후기 게시글 신고 기능 추가
    @Operation(summary = "게시글 신고",
            description = "게시판 이용자는 " +
            "\n스팸/욕설/음란물/사기/잘못된정보/기타로 게시글을 신고할 수 있다. " +
            "\n게시판 이용자는 기타카테고리 선택할 시에는 상세한 내용을 필시 작성한다.")
    @PostMapping("/{postId}/report")
    public ResponseEntity<?> reportVolunteerPost(
            @PathVariable Integer postId,
            @RequestBody VolunteerPostReportCreateRequest req
    ) {
        try {
            req.setPostId(postId); // path로 고정
            Integer id = volunteerPostService.reportVolunteerPost(req);
            return ResponseEntity.ok(Map.of("reportId", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 댓글 작성
    @Operation(summary = "댓글 등록",
            description = "일반회원과 보호소장은 봉사후기 게시글을 작성할 수 있다.")
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
            description = "일반회원과 보호소장은 자신이 작성한 봉사후기 게시글을 수정할 수 있다.")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> modifyVolunteerPostComment(
            @PathVariable Integer commentId,
            @RequestBody VolunteerPostCommentUpdateDTO dto
    ) {
        volunteerPostService.modifyVolunteerPostComment(commentId, dto);
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제(soft)
    @Operation(summary = "댓글 삭제",
            description = "일반회원과 보호소장은 자신이 작성한 봉사후기 게시글을 삭제할 수 있다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteVolunteerPostComment(@PathVariable Integer commentId) {
        volunteerPostService.deleteVolunteerPostComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 신고
//    @PostMapping("/comments/{commentId}/report")
//    public ResponseEntity<Void> reportComment(
//            @PathVariable Integer commentId,
//            @RequestBody VolunteerPostCommentReportRequest req
//    ) {
//        req.setCommentId(commentId);
//        volunteerPostService.reportComment(req);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

    // 댓글 신고 기능 추가
    @Operation(summary = "댓글 신고",
            description = "게시판 이용자는 " +
            "\n스팸/욕설/음란물/사기/잘못된정보/기타로 댓글을 신고할 수 있다. " +
            "\n게시판 이용자는 기타카테고리 선택할 시에는 상세한 내용을 필히 작성한다.")
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
