package com.backoven.catdogshelter.domain.notice.command.domain.repository;

import com.backoven.catdogshelter.domain.notice.command.domain.aggregate.entity.NoticeFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NoticeFileRepository extends JpaRepository<NoticeFileEntity, Integer> {

    // 삭제할 파일들을 번호로 찾음
    List<NoticeFileEntity> findByIdIn(List<Integer> deleteFileIds);

    List<NoticeFileEntity> findByNoticeId(Integer id);
}
