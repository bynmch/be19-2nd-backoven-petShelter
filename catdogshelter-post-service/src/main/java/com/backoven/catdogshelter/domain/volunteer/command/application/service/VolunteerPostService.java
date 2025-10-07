package com.backoven.catdogshelter.domain.volunteer.command.application.service;

import com.backoven.catdogshelter.domain.volunteer.command.application.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VolunteerPostService {

    // 게시글 작성 + 수정 + 삭제
    Integer writeVolunteerPost(VolunteerPostCreateDTO dto, List<MultipartFile> files);
    void modifyVolunteerPost(Integer postId, VolunteerPostUpdateDTO dto, List<MultipartFile> newFiles);
    void deleteVolunteerPost(Integer postId);

    // 좋아요 토글: true=좋아요됨, false=해제됨
    boolean toggleLike(Integer postId, VolunteerPostLikeToggleRequest req);

    // 댓글
    Integer addVolunteerPostComment(VolunteerPostCommentCreateDTO dto);
    void modifyVolunteerPostComment(Integer commentId, VolunteerPostCommentUpdateDTO dto);
    void deleteVolunteerPostComment(Integer commentId);

    // 신고
    Integer reportVolunteerPost(VolunteerPostReportCreateRequest req);
    Integer reportVolunteerPostComment(VolunteerPostCommentReportCreateRequest req);
}
