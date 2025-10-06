package com.backoven.catdogshelter.domain.volunteer.command.application.service;

import com.backoven.catdogshelter.common.entity.ShelterHeadEntity;
import com.backoven.catdogshelter.common.entity.UserEntity;
import com.backoven.catdogshelter.common.repository.VolNoShelterHeadRepository;
import com.backoven.catdogshelter.common.repository.VolNoUserRepository;
import com.backoven.catdogshelter.common.util.DateTimeUtil;
import com.backoven.catdogshelter.common.util.ReportCategory;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.*;
import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.*;
import com.backoven.catdogshelter.domain.volunteer.command.domain.repository.*;
import com.backoven.catdogshelter.domain.volunteer.command.infrastructure.util.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class VolunteerPostServiceImpl implements VolunteerPostService {

    private final VolunteerPostRepository volunteerPostRepository;
    private final VolunteerPostFileRepository volunteerPostFileRepository;
    private final VolunteerPostLikedRepository volunteerPostLikedRepository;
    private final VolunteerPostReportRepository volunteerPostReportRepository;
    private final VolunteerPostCommentRepository volunteerPostCommentRepository;
    private final VolunteerPostCommentReportRepository volunteerPostCommentReportRepository;
    private final VolunteerAssociationApplicationDetailsRepository applicationDetailsRepository;

    private final VolNoUserRepository volNoUserRepository;
    private final VolNoShelterHeadRepository headRepository;
    private final FileStorage fileStorage;

    @Autowired
    public VolunteerPostServiceImpl(VolunteerPostRepository volunteerPostRepository,
                                    VolunteerPostFileRepository volunteerPostFileRepository,
                                    VolunteerPostLikedRepository volunteerPostLikedRepository,
                                    VolunteerPostReportRepository volunteerPostReportRepository,
                                    VolunteerPostCommentRepository volunteerPostCommentRepository,
                                    VolunteerPostCommentReportRepository volunteerPostCommentReportRepository,
                                    VolunteerAssociationApplicationDetailsRepository applicationDetailsRepository,
                                    VolNoUserRepository VolNoUserRepository,
                                    VolNoShelterHeadRepository headRepository,
                                    FileStorage fileStorage) {
        this.volunteerPostRepository = volunteerPostRepository;
        this.volunteerPostFileRepository = volunteerPostFileRepository;
        this.volunteerPostLikedRepository = volunteerPostLikedRepository;
        this.volunteerPostReportRepository = volunteerPostReportRepository;
        this.volunteerPostCommentRepository = volunteerPostCommentRepository;
        this.volunteerPostCommentReportRepository = volunteerPostCommentReportRepository;
        this.applicationDetailsRepository = applicationDetailsRepository;
        this.volNoUserRepository = VolNoUserRepository;
        this.headRepository = headRepository;
        this.fileStorage = fileStorage;
    }

    // 봉사후기 작성 + 파일 등록
    @Override
    public Integer writeVolunteerPost(VolunteerPostCreateDTO dto, List<MultipartFile> files) {
        var detail = applicationDetailsRepository.findById(dto.getVolappdetailId())
                .orElseThrow(() -> new IllegalArgumentException("신청내역이 없습니다: " + dto.getVolappdetailId()));

        var post = VolunteerPostEntity.newPost(dto.getTitle(), dto.getContent(), detail);
        // 파일 저장
        var stored = fileStorage.storeAll(files);
        for (var s : stored) {
            var f = new VolunteerPostFileEntity();
            f.setPost(post);
            f.setFileRename(s.getFileRename());
            f.setFilePath(s.getFilePath());
            f.setUploadedAt(s.getUploadedAt());
            post.getFiles().add(f);
        }
        volunteerPostRepository.save(post);
        return post.getId();
    }

    // 봉사후기 수정 + 파일 수정
    @Override
    public void modifyVolunteerPost(Integer postId, VolunteerPostUpdateDTO dto, List<MultipartFile> newFiles) {
        var post = volunteerPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + postId));
        if (post.getDeleted()) throw new IllegalStateException("삭제된 게시글입니다.");

        post.modify(dto.getTitle(), dto.getContent());

        // 파일 삭제
        if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
            var targets = volunteerPostFileRepository.findByIdIn(dto.getDeleteFileIds());
            // 소유 검증
            for (var f : targets) {
                if (!Objects.equals(f.getPost().getId(), post.getId()))
                    throw new IllegalArgumentException("다른 게시글 파일은 삭제할 수 없습니다. fileId=" + f.getId());
            }
            log.info("파일 확인: {}", targets);
            volunteerPostFileRepository.deleteAll(targets);
        }

        // 새 파일 추가
        var stored = fileStorage.storeAll(newFiles);
        for (var s : stored) {
            var f = new VolunteerPostFileEntity();
            f.setPost(post);
            f.setFileRename(s.getFileRename());
            f.setFilePath(s.getFilePath());
            f.setUploadedAt(s.getUploadedAt());
            volunteerPostFileRepository.save(f);
        }
    }

    // 봉사후기 삭제
    @Override
    public void deleteVolunteerPost(Integer postId) {
        var updated = volunteerPostRepository.softDelete(postId, DateTimeUtil.now());
        if (updated == 0) throw new IllegalArgumentException("이미 삭제되었거나 존재하지 않습니다: " + postId);
    }

    @Override
    public boolean toggleLike(Integer postId, VolunteerPostLikeToggleRequest req) {
        var post = volunteerPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + postId));
        if (req.getActorType() == VolunteerPostLikeToggleRequest.ActorType.USER) {
            var user = volNoUserRepository.findById(req.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + req.getUserId()));
            if (volunteerPostLikedRepository.existsByPost_IdAndUser_UserId(postId, user.getUserId())) {
                volunteerPostLikedRepository.deleteByPost_IdAndUser_UserId(postId, user.getUserId());
                return false;
            } else {
                var e = new VolunteerPostLikedEntity();
                e.setPost(post);
                e.setUser(user);
                volunteerPostLikedRepository.save(e);
                return true;
            }
        } else {
            var head = headRepository.findById(req.getHeadId())
                    .orElseThrow(() -> new IllegalArgumentException("보호소장 없음: " + req.getHeadId()));
            if (volunteerPostLikedRepository.existsByPost_IdAndHead_Id(postId, head.getId())) {
                volunteerPostLikedRepository.deleteByPost_IdAndHead_Id(postId, head.getId());
                return false;
            } else {
                var e = new VolunteerPostLikedEntity();
                e.setPost(post);
                e.setHead(head);
                volunteerPostLikedRepository.save(e);
                return true;
            }
        }
    }

    // 봉사후기 게시글 신고
//    @Override
//    public void reportPost(VolunteerPostReportRequest req) {
//        var post = volunteerPostRepository.findById(req.getPostId())
//                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + req.getPostId()));
//
//        if (req.getActorType() == VolunteerPostLikeToggleRequest.ActorType.USER) {
//            var user = VolNoUserRepository.findById(req.getUserId())
//                    .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + req.getUserId()));
//            if (volunteerPostReportRepository.existsByPost_IdAndUser_UserId(post.getId(), user.getUserId()))
//                return; // 이미 신고함
//            var e = VolunteerPostReportEntity.create(post, req.getCategory(), req.getEtcDetail(), user, null);
//            volunteerPostReportRepository.save(e);
//        } else {
//            var head = headRepository.findById(req.getHeadId())
//                    .orElseThrow(() -> new IllegalArgumentException("보호소장 없음: " + req.getHeadId()));
//            if (volunteerPostReportRepository.existsByPost_IdAndHead_Id(post.getId(), head.getId()))
//                return;
//            var e = VolunteerPostReportEntity.create(post, req.getCategory(), req.getEtcDetail(), null, head);
//            volunteerPostReportRepository.save(e);
//        }
//    }

    // 봉사후기 게시글 etc만 내용 넣을 수 있도록 유효성 검사 로직 추기
    // 문자열 -> enum 안전 변환 (대소문자 허용)
    private ReportCategory toCategory(String raw) {
        try {
            return ReportCategory.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 카테고리: " + raw);
        }
    }
    @Override
    public Integer reportVolunteerPost(VolunteerPostReportCreateRequest req) {
        // 필수값 체크
        if (req.getPostId() == null) throw new IllegalArgumentException("postId는 필수입니다.");
        if ((req.getUserId() == null) == (req.getHeadId() == null)) {
            // 둘 다 null이거나 둘 다 채워진 경우
            throw new IllegalArgumentException("신고 주체는 userId 또는 headId 중 정확히 하나만 보내세요.");
        }
        final ReportCategory category = toCategory(req.getCategory());

        // ETC 규칙: ETC면 etcDetail 필수, 아니면 null로 강제
        String etcDetailToSave = null;
        if (category == ReportCategory.ETC) {
            if (req.getEtcDetail() == null || req.getEtcDetail().isBlank()) {
                throw new IllegalArgumentException("category=ETC 인 경우 etcDetail이 필요합니다.");
            }
            etcDetailToSave = req.getEtcDetail();
        }

        var post = volunteerPostRepository.findById(req.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("post가 존재하지 않습니다. id=" + req.getPostId()));

        var entity = new VolunteerPostReportEntity();
        entity.setPost(post);
        entity.setCategory(category);
        entity.setEtcDetail(etcDetailToSave);      // ETC가 아니면 null
        entity.setCreatedAt(DateTimeUtil.now());
        entity.setStatus(false);

        if (req.getUserId() != null) {
            entity.setUser(volNoUserRepository.getReferenceById(req.getUserId()));
        } else {
            entity.setHead(headRepository.getReferenceById(req.getHeadId()));
        }

        try {
            volunteerPostReportRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("uq_volunteer_comment_report_head")) {
                throw new IllegalStateException("이미 신고한 댓글입니다. (head)");
            }
            if (e.getMessage() != null && e.getMessage().contains("uq_volunteer_comment_report_user")) {
                throw new IllegalStateException("이미 신고한 댓글입니다. (user)");
            }
            throw e; // 다른 제약 위반은 그대로 터뜨림
        };

        return entity.getId();
    }

    // 댓글 추가
    @Override
    public Integer addVolunteerPostComment(VolunteerPostCommentCreateDTO dto) {
        var post = volunteerPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + dto.getPostId()));

        UserEntity user = null; ShelterHeadEntity head = null;
        if (dto.getActorType() == VolunteerPostLikeToggleRequest.ActorType.USER) {
            user = volNoUserRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + dto.getUserId()));
        } else {
            head = headRepository.findById(dto.getHeadId())
                    .orElseThrow(() -> new IllegalArgumentException("보호소장 없음: " + dto.getHeadId()));
        }

        var comment = VolunteerPostCommentEntity.create(post, dto.getContent(), user, head);
        volunteerPostCommentRepository.save(comment);
        return comment.getId();
    }

    // 댓글 수정
    @Override
    public void modifyVolunteerPostComment(Integer commentId, VolunteerPostCommentUpdateDTO dto) {
        var c = volunteerPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + commentId));
        if (c.getDeleted()) throw new IllegalStateException("삭제된 댓글입니다.");
        c.edit(dto.getContent());
    }

    // 댓글 삭제 (soft)
    @Override
    public void deleteVolunteerPostComment(Integer commentId) {
        var updated = volunteerPostCommentRepository.softDelete(commentId, DateTimeUtil.now());
        if (updated == 0) throw new IllegalArgumentException("이미 삭제되었거나 존재하지 않습니다: " + commentId);
    }

    // 댓글 신고
//    @Override
//    public void reportComment(VolunteerPostCommentReportRequest req) {
//        var c = volunteerPostCommentRepository.findById(req.getCommentId())
//                .orElseThrow(() -> new IllegalArgumentException("댓글 없음: " + req.getCommentId()));
//
//        if (req.getActorType() == VolunteerPostLikeToggleRequest.ActorType.USER) {
//            var user = VolNoUserRepository.findById(req.getUserId())
//                    .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + req.getUserId()));
//            if (volunteerPostCommentReportRepository.existsByComment_IdAndUser_UserId(c.getId(), user.getUserId()))
//                return;
//            var e = VolunteerPostCommentReportEntity.create(c, req.getCategory(), req.getEtcDetail(), user, null);
//            volunteerPostCommentReportRepository.save(e);
//        } else {
//            var head = headRepository.findById(req.getHeadId())
//                    .orElseThrow(() -> new IllegalArgumentException("보호소장 없음: " + req.getHeadId()));
//            if (volunteerPostCommentReportRepository.existsByComment_IdAndHead_Id(c.getId(), head.getId()))
//                return;
//            var e = VolunteerPostCommentReportEntity.create(c, req.getCategory(), req.getEtcDetail(), null, head);
//            volunteerPostCommentReportRepository.save(e);
//        }
//    }

    // 댓글도 etc만 입력 가능,
    @Override
    public Integer reportVolunteerPostComment(VolunteerPostCommentReportCreateRequest req) {
        if (req.getCommentId() == null) throw new IllegalArgumentException("commentId는 필수입니다.");
        if ((req.getUserId() == null) == (req.getHeadId() == null)) {
            throw new IllegalArgumentException("신고 주체는 userId 또는 headId 중 정확히 하나만 보내세요.");
        }
        final ReportCategory category = toCategory(req.getCategory());

        String etcDetailToSave = null;
        if (category == ReportCategory.ETC) {
            if (req.getEtcDetail() == null || req.getEtcDetail().isBlank()) {
                throw new IllegalArgumentException("category=ETC 인 경우 etcDetail이 필요합니다.");
            }
            etcDetailToSave = req.getEtcDetail();
        }

        var comment = volunteerPostCommentRepository.findById(req.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("comment가 존재하지 않습니다. id=" + req.getCommentId()));

        var entity = new VolunteerPostCommentReportEntity();
        entity.setComment(comment);
        entity.setCategory(category);
        entity.setEtcDetail(etcDetailToSave);  // ETC가 아니면 null
        entity.setCreatedAt(DateTimeUtil.now());
        entity.setStatus(false);

        if (req.getUserId() != null) {
            entity.setUser(volNoUserRepository.getReferenceById(req.getUserId()));
        } else {
            entity.setHead(headRepository.getReferenceById(req.getHeadId()));
        }

        try {
            volunteerPostCommentReportRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("uq_volunteer_comment_report_head")) {
                throw new IllegalStateException("이미 신고한 댓글입니다. (head)");
            }
            if (e.getMessage() != null && e.getMessage().contains("uq_volunteer_comment_report_user")) {
                throw new IllegalStateException("이미 신고한 댓글입니다. (user)");
            }
            throw e; // 다른 제약 위반은 그대로 터뜨림
        }
        return entity.getId();
    }
}
