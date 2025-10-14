package com.backoven.catdogshelter.domain.volunteer.command.application.controller;

import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationApplyRequest;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationApproveRequest;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationDTO;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationUpdateDTO;
import com.backoven.catdogshelter.domain.volunteer.command.application.service.VolunteerAssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "봉사모임 API")
@RestController
@RequestMapping("/association-posts")
public class VolunteerAssociationCommandController {


    private final VolunteerAssociationService volunteerAssociationService;

    @Autowired
    public VolunteerAssociationCommandController(VolunteerAssociationService volunteerAssociationService) {
        this.volunteerAssociationService = volunteerAssociationService;
    }

    // 게시글 등록
    @Operation(summary = "게시글 등록",
            description = "보호소장은 봉사모임 게시글을 작성할 수 있다.")
    @PostMapping
    public ResponseEntity<Integer> writeAssociation(@RequestBody VolunteerAssociationDTO dto) {
        Integer id = volunteerAssociationService.writeAssociation(dto);
        return ResponseEntity.ok(id);
    }

    // 게시글 수정
    @Operation(summary = "게시글 수정",
            description = "보호소장은 자신이 작성한 게시글을 수정할 수 있다.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyAssociation(@PathVariable Integer id,
                                       @RequestBody VolunteerAssociationUpdateDTO dto) {
        volunteerAssociationService.modifyAssociation(id, dto);
        return ResponseEntity.ok().build();
    }

    // 삭제 => 마감(soft delete)
//    @DeleteMapping("/{id}/delete")
//    public ResponseEntity<Void> deleteAssociation(@PathVariable Integer id) {
//        service.deleteAssociation(id); // deadline=true
//        return ResponseEntity.ok().build();
//    }

    // 신청
    @Operation(summary = "신청",
            description = "일반회원은 봉사모임 게시글을 보고 신청할 수 있다.")
    @PostMapping("/apply")
    public ResponseEntity<Integer> apply(@RequestBody VolunteerAssociationApplyRequest req) {
        Integer id = volunteerAssociationService.apply(req);
        return ResponseEntity.ok(id);
    }

    // 신청 취소
    @Operation(summary = "신청 취소",
            description = "일반회원은 신청을 취소할 수 있다.")
    @DeleteMapping("/apply")
    public ResponseEntity<Void> cancel(@RequestBody VolunteerAssociationApplyRequest req) {
        volunteerAssociationService.cancel(req);
        return ResponseEntity.ok().build();
    }

    // 승인(반려 없음)
    @Operation(summary = "신청 승인",
            description = "일반회원의 신청을 승인할 수 있다.")
    @PostMapping("/apply/approve")
    public ResponseEntity<Void> approve(@RequestBody VolunteerAssociationApproveRequest req) {
        volunteerAssociationService.approve(req);
        return ResponseEntity.ok().build();
    }

    // 종료 처리(선택)
//    @PostMapping("/{id}/end")
//    public ResponseEntity<Void> end(@PathVariable Integer id) {
//        service.endAssociation(id);
//        return ResponseEntity.ok().build();
//    }
}
