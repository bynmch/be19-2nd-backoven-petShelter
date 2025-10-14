package com.backoven.catdogshelter.domain.volunteer.command.application.service;

import com.backoven.catdogshelter.common.entity.ShelterheadEntity;
import com.backoven.catdogshelter.common.entity.SigunguEntity;
import com.backoven.catdogshelter.common.entity.UserEntity;
import com.backoven.catdogshelter.common.util.DateTimeUtil;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationApplyRequest;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationApproveRequest;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationDTO;
import com.backoven.catdogshelter.domain.volunteer.command.application.dto.VolunteerAssociationUpdateDTO;
import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.VolunteerAssociationApplicationDetailsEntity;
import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.VolunteerAssociationEntity;
import com.backoven.catdogshelter.domain.volunteer.command.domain.repository.VolunteerAssociationApplicationDetailsRepository;
import com.backoven.catdogshelter.domain.volunteer.command.domain.repository.VolunteerAssociationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@Transactional
public class VolunteerAssociationServiceImpl implements VolunteerAssociationService {

    private final VolunteerAssociationRepository associationRepository;
    private final VolunteerAssociationApplicationDetailsRepository applicationRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public VolunteerAssociationServiceImpl(VolunteerAssociationRepository associationRepository, VolunteerAssociationApplicationDetailsRepository applicationRepository) {
        this.associationRepository = associationRepository;
        this.applicationRepository = applicationRepository;
    }

    private boolean isPast(String startDateStr) {
        if (startDateStr == null || startDateStr.isBlank()) return false;

        // 지원할 포맷들(필요시 추가)
        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd"
        };
        for (String p : patterns) {
            try {
                LocalDateTime start = LocalDateTime.parse(startDateStr, DateTimeFormatter.ofPattern(p));
                return start.isBefore(LocalDateTime.now());
            } catch (DateTimeParseException ignored) {}
        }
        // 모두 실패하면 자동마감하지 않음(데이터 형식 점검 필요)
        return false;
    }

    @Override
    public Integer writeAssociation(VolunteerAssociationDTO dto) {
        var assoc = new VolunteerAssociationEntity();
        assoc.setTitle(dto.getTitle());
        assoc.setContent(dto.getContent());
        assoc.setTime(dto.getTime());
        assoc.setStartDate(dto.getStartDate());
        assoc.setDetailAddress(dto.getDetailAddress());
        assoc.setDeadline(false);
        assoc.setNumberOfPeople(dto.getNumberOfPeople());
        assoc.setIsEnd(false);
        assoc.setCreatedAt(DateTimeUtil.now());
        assoc.setHead(em.getReference(ShelterheadEntity.class, dto.getHeadId()));
        assoc.setSigungu(em.getReference(SigunguEntity.class, dto.getSigunguId()));
        associationRepository.save(assoc);
        return assoc.getVolunteerId();
    }

    @Override
    public void modifyAssociation(Integer id, VolunteerAssociationUpdateDTO dto) {
        var assoc = associationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("봉사모임을 찾을 수 없습니다. id=" + id));

        if (dto.getTitle() != null) assoc.setTitle(dto.getTitle());
        if (dto.getContent() != null) assoc.setContent(dto.getContent());
        if (dto.getTime() != null) assoc.setTime(dto.getTime());
        if (dto.getStartDate() != null) {
            assoc.setStartDate(dto.getStartDate());
            // 🔹 startDate를 바꿀 때 자동 마감 다시 평가
            boolean autoDeadline = isPast(dto.getStartDate());
            // dto.deadline이 명시되면 그것을 우선, 없으면 자동마감 적용
            if (dto.getDeadline() != null) {
                assoc.setDeadline(dto.getDeadline() || autoDeadline);
            } else {
                assoc.setDeadline(autoDeadline || Boolean.TRUE.equals(assoc.getDeadline()));
            }
        } else {
            // startDate 변경이 없으면, dto.deadline이 명시된 경우만 반영
            if (dto.getDeadline() != null) {
                assoc.setDeadline(dto.getDeadline());
            }
        }

        if (dto.getDetailAddress() != null) assoc.setDetailAddress(dto.getDetailAddress());
        if (dto.getNumberOfPeople() != null) assoc.setNumberOfPeople(dto.getNumberOfPeople());
        if (dto.getIsEnd() != null) assoc.setIsEnd(dto.getIsEnd());
    }

//    /** 삭제 = 마감 처리(soft delete) */
    @Override
    public void deleteAssociation(Integer id) {
        var assoc = associationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("봉사모임을 찾을 수 없습니다. id=" + id));
        assoc.setDeadline(true); // 신청 마감만 시킴 (물리 삭제 X)
    }

    // 봉사 신청
    @Override
    public Integer apply(VolunteerAssociationApplyRequest req) {
        var assoc = associationRepository.findById(req.getVolunteerId())
                .orElseThrow(() -> new IllegalArgumentException("봉사모임 없음: " + req.getVolunteerId()));

        if (Boolean.TRUE.equals(assoc.getIsEnd())) {
            throw new IllegalStateException("종료된 봉사모임입니다.");
        }
        if (Boolean.TRUE.equals(assoc.getDeadline())) {
            throw new IllegalStateException("신청 마감된 봉사모임입니다.");
        }

        if (assoc.getNumberOfPeople() != null) {
            long applied = applicationRepository.countByVolunteer(req.getVolunteerId());
            if (applied >= assoc.getNumberOfPeople()) {
                throw new IllegalStateException("정원이 가득 찼습니다.");
            }
        }

        boolean exists = applicationRepository.existsByVolunteerAndUser(req.getVolunteerId(), req.getUserId());
        if (exists) {
            throw new IllegalStateException("이미 신청했습니다.");
        }

        var app = new VolunteerAssociationApplicationDetailsEntity();
        app.setVolunteer(assoc);
        app.setUser(em.getReference(UserEntity.class, req.getUserId()));
        app.setStatus(false); // 대기
        app.setTime(0);

        assoc.getApplications().add(app);

        applicationRepository.save(app);
        return app.getId();
    }

    // 신청 취소
    @Override
    public void cancel(VolunteerAssociationApplyRequest req) {
        int deleted = applicationRepository.deleteByVolunteerAndUser(req.getVolunteerId(), req.getUserId());
        if (deleted == 0) {
            throw new IllegalArgumentException("신청내역이 없습니다.");
        }
    }

    /* 신청 승인 */
    @Override
    public void approve(VolunteerAssociationApproveRequest req) {
        var app = applicationRepository.findById(req.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("신청내역 없음: " + req.getApplicationId()));
        app.setStatus(true);
        app.setTime(req.getTime() != null ? req.getTime() : 0);
    }

//    @Override
    public void endAssociation(Integer id) {
        var assoc = associationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("봉사모임을 찾을 수 없습니다. id=" + id));
        assoc.setIsEnd(true);
        assoc.setDeadline(true); // 종료시 신청도 마감
    }
}
