package com.backoven.catdogshelter.domain.volunteer.command.application.service;

import com.backoven.catdogshelter.common.entity.ShelterheadEntity;
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

    // 게시글 등록
    @Override
    public Integer writeVolunteerPost(VolunteerPostCreateDTO dto, List<MultipartFile> files) {
        VolunteerAssociationApplicationDetailsEntity detail =
                applicationDetailsRepository.findById(dto.getVolappdetailId())
                .orElseThrow(() -> new IllegalArgumentException("신청내역이 없습니다: " + dto.getVolappdetailId()));

        VolunteerPostEntity post =
                VolunteerPostEntity.newPost(dto.getTitle(), dto.getContent(), detail);

        // 파일 저장
        List<VolunteerPostFileDTO> stored = fileStorage.storeAll(files);
        for (VolunteerPostFileDTO vPFDto : stored) {
            VolunteerPostFileEntity vPFE = new VolunteerPostFileEntity();
            vPFE.setPost(post);
            vPFE.setFileRename(vPFDto.getFileRename());
            vPFE.setFilePath(vPFDto.getFilePath());
            vPFE.setUploadedAt(vPFDto.getUploadedAt());
            post.getFiles().add(vPFE);
        }
        volunteerPostRepository.save(post);
        return post.getId();
    }

    // 게시글 수정
    @Override
    public void modifyVolunteerPost(Integer postId, VolunteerPostUpdateDTO dto, List<MultipartFile> newFiles) {
        VolunteerPostEntity post = volunteerPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + postId));
        if (post.getDeleted()) throw new IllegalStateException("삭제된 게시글입니다.");

        post.modify(dto.getTitle(), dto.getContent());

        for (VolunteerPostFileEntity file : post.getFiles()) {
            dto.getDeleteFileIds().add(file.getId());
        }

        // 파일 삭제
        if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
            List<VolunteerPostFileEntity> targets = volunteerPostFileRepository.findByIdIn(dto.getDeleteFileIds());

            // 소유 검증
            for (VolunteerPostFileEntity file : targets) {
                if (!Objects.equals(file.getPost().getId(), post.getId()))
                    throw new IllegalArgumentException("다른 게시글 파일은 삭제할 수 없습니다. fileId=" + file.getId());
                post.getFiles().remove(file);
            }
            volunteerPostFileRepository.deleteAll(targets);
        }

        // 새 파일 추가
        List<VolunteerPostFileDTO> stored = fileStorage.storeAll(newFiles);
        for (VolunteerPostFileDTO fileDTO : stored) {
            VolunteerPostFileEntity fileEntity = new VolunteerPostFileEntity();
            fileEntity.setPost(post);
            fileEntity.setFileRename(fileDTO.getFileRename());
            fileEntity.setFilePath(fileDTO.getFilePath());
            fileEntity.setUploadedAt(fileDTO.getUploadedAt());
            volunteerPostFileRepository.save(fileEntity);
        }
    }

    // 게시글 삭제
    @Override
    public void deleteVolunteerPost(Integer postId) {
        VolunteerPostEntity post = volunteerPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        // 게시글만 Soft 삭제처리
        post.setDeleted(true);

        // 파일 삭제
        List<VolunteerPostFileEntity> files = volunteerPostFileRepository.findByPostId(postId);
        volunteerPostFileRepository.deleteAll(files);
    }

    // 게시글 추천
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
            ShelterheadEntity head = headRepository.findById(req.getHeadId())
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

    // 게시글 신고
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
            throw e;
        };

        return entity.getId();
    }

    // 댓글 추가
    @Override
    public Integer addVolunteerPostComment(VolunteerPostCommentCreateDTO dto) {
        var post = volunteerPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + dto.getPostId()));

        UserEntity user = null;
        ShelterheadEntity head = null;
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
            throw e;
        }
        return entity.getId();
    }
}
