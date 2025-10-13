package com.backoven.catdogshelter.domain.notice.command.application.service;

import com.backoven.catdogshelter.common.entity.ShelterheadEntity;
import com.backoven.catdogshelter.common.entity.UserEntity;
import com.backoven.catdogshelter.common.repository.VolNoShelterHeadRepository;
import com.backoven.catdogshelter.common.repository.VolNoUserRepository;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeCreateDTO;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeFileDTO;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeLikeToggleRequest;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeUpdateDTO;
import com.backoven.catdogshelter.domain.notice.command.domain.aggregate.entity.NoticeEntity;
import com.backoven.catdogshelter.domain.notice.command.domain.aggregate.entity.NoticeFileEntity;
import com.backoven.catdogshelter.domain.notice.command.domain.aggregate.entity.NoticeLikedEntity;
import com.backoven.catdogshelter.domain.notice.command.domain.repository.NoticeFileRepository;
import com.backoven.catdogshelter.domain.notice.command.domain.repository.NoticeLikedRepository;
import com.backoven.catdogshelter.domain.notice.command.domain.repository.NoticeRepository;
import com.backoven.catdogshelter.domain.notice.command.infrastructure.util.FileStorage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Transactional
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final NoticeLikedRepository noticeLikedRepository;
    private final VolNoUserRepository volNoUserRepository;
    private final VolNoShelterHeadRepository volNoShelterHeadRepository;
    @Qualifier("noticeFileStorage")
    private final FileStorage fileStorage;

    @Autowired
    public NoticeServiceImpl(NoticeRepository noticeRepository,
                             NoticeFileRepository noticeFileRepository,
                             NoticeLikedRepository noticeLikedRepository,
                             VolNoUserRepository volNoUserRepository,
                             VolNoShelterHeadRepository volNoShelterHeadRepository, FileStorage fileStorage) {
        this.noticeRepository = noticeRepository;
        this.noticeFileRepository = noticeFileRepository;
        this.noticeLikedRepository = noticeLikedRepository;
        this.volNoUserRepository = volNoUserRepository;
        this.volNoShelterHeadRepository = volNoShelterHeadRepository;
        this.fileStorage = fileStorage;
    }

    // 게시글 등록
    @Override
    public Integer writeNotice(NoticeCreateDTO dto, List<MultipartFile> files) {

        NoticeEntity notice = NoticeEntity.newNotice(dto.getTitle(), dto.getContent());
        // 파일 저장
        List<NoticeFileDTO> stored = fileStorage.storeAll(files);
        for (NoticeFileDTO nD : stored) {
            NoticeFileEntity nFE = new NoticeFileEntity();
            nFE.setNotice(notice);
            nFE.setFileRename(nD.getFileRename());
            nFE.setFilePath(nD.getFilePath());
            nFE.setUploadedAt(nD.getUploadedAt());
            notice.getFiles().add(nFE);
        }
        noticeRepository.save(notice);
        return notice.getId();
    }

    // 게시글 수정
    @Override
    public void modifyNotice(Integer id, NoticeUpdateDTO dto, List<MultipartFile> newFiles) {
        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + id));

        notice.modify(dto.getTitle(), dto.getContent());

        for (NoticeFileEntity file : notice.getFiles()) {
            dto.getDeleteFileIds().add(file.getId());
        }

        log.info("수정할 파일 번호: {}", dto.getDeleteFileIds());

        // 파일 삭제
        if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
            List<NoticeFileEntity> targets = noticeFileRepository.findByIdIn(dto.getDeleteFileIds());
            log.info("삭제할 파일 확인: {}", targets);
            for (NoticeFileEntity nFE : targets) {
                if (!Objects.equals(nFE.getNotice().getId(), notice.getId()))
                    throw new IllegalArgumentException("다른 게시글 파일은 삭제할 수 없습니다. fileId=" + nFE.getId());
                notice.getFiles().remove(nFE);
            }
            noticeFileRepository.deleteAll(targets);
            log.info("파일이 삭제되었는지 확인: {}", targets);
        }


        // 새 파일 추가
        List<NoticeFileDTO> stored = fileStorage.storeAll(newFiles);
        for (NoticeFileDTO nFD : stored) {
            NoticeFileEntity nFE = new NoticeFileEntity();
            nFE.setNotice(notice);
            nFE.setFileRename(nFD.getFileRename());
            nFE.setFilePath(nFD.getFilePath());
            nFE.setUploadedAt(nFD.getUploadedAt());
            noticeFileRepository.save(nFE);
        }
    }

    // 게시글 삭제
    @Override
    public void deleteNotice(Integer id) {
        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        // 게시글만 Soft삭제처리
        notice.setIsDeleted(true);

        // 파일 삭제
        List<NoticeFileEntity> files = noticeFileRepository.findByNoticeId(id);
        noticeFileRepository.deleteAll(files);
    }

    // 게시글 추천
    @Override
    public boolean toggleLike(Integer noticeId, NoticeLikeToggleRequest request) {
        NoticeEntity notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + noticeId));
        if (request.getActorType() == NoticeLikeToggleRequest.ActorType.USER) {
            UserEntity user = volNoUserRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("회원 없음: " + request.getUserId()));
            if (noticeLikedRepository.existsByNotice_IdAndUser_UserId(noticeId, user.getUserId())) {
                noticeLikedRepository.deleteByNotice_IdAndUser_UserId(noticeId, user.getUserId());
                return false;
            } else {
                NoticeLikedEntity like = new NoticeLikedEntity();
                like.setNotice(notice);
                like.setUser(user);
                noticeLikedRepository.save(like);
                return true;
            }
        } else {
            ShelterheadEntity head = volNoShelterHeadRepository.findById(request.getHeadId())
                    .orElseThrow(() -> new IllegalArgumentException("보호소장 없음: " + request.getHeadId()));
            if (noticeLikedRepository.existsByNotice_IdAndHead_Id(noticeId, head.getId())) {
                noticeLikedRepository.deleteByNotice_IdAndHead_Id(noticeId, head.getId());
                return false;
            } else {
                NoticeLikedEntity like = new NoticeLikedEntity();
                like.setNotice(notice);
                like.setHead(head);
                noticeLikedRepository.save(like);
                return true;
            }
        }
    }


}
