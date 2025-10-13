package com.backoven.catdogshelter.domain.notice.command.domain.repository;

import com.backoven.catdogshelter.domain.notice.command.domain.aggregate.entity.NoticeLikedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeLikedRepository extends JpaRepository<NoticeLikedEntity, Integer> {

    boolean existsByNotice_IdAndUser_UserId(Integer noticeId, Integer userId);
    boolean existsByNotice_IdAndHead_Id(Integer noticeId, Integer headId);

    int deleteByNotice_IdAndUser_UserId(Integer noticeId, Integer userId);
    int deleteByNotice_IdAndHead_Id(Integer noticeId, Integer headId);
}
