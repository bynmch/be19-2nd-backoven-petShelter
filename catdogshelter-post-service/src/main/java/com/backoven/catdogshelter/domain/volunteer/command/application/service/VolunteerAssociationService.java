package com.backoven.catdogshelter.domain.volunteer.command.application.service;

import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationApplyRequest;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationApproveRequest;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationDTO;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationUpdateDTO;

public interface VolunteerAssociationService {

    /** 게시글 등록 */
    Integer writeAssociation(VolunteerAssociationDTO dto);

    /** 게시글 수정 */
    void modifyAssociation(Integer id, VolunteerAssociationUpdateDTO dto);

    /** 게시글 삭제 */
    void deleteAssociation(Integer id);

//    /** 일반회원 신청 */
    Integer apply(VolunteerAssociationApplyRequest req);

//    /** 일반회원 신청 취소 */
    void cancel(VolunteerAssociationApplyRequest req);

//    /** 보호소장 승인만 존재 */
    void approve(VolunteerAssociationApproveRequest req);

//    /** 모임 종료 처리 (is_end = true) – 필요 시 유지 */
    void endAssociation(Integer id);
}
