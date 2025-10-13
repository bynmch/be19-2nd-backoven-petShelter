package com.backoven.catdogshelter.domain.notice.command.domain.repository;

import com.backoven.catdogshelter.domain.notice.command.domain.aggregate.entity.NoticeLikedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeLikedRepository extends JpaRepository<NoticeLikedEntity, Integer> {
    boolean existsByNotice_IdAndUser_UserId(Integer postId, Integer userId);
    boolean existsByNotice_IdAndHead_Id(Integer postId, Integer headId);

    Integer deleteByNotice_IdAndUser_UserId(Integer postId, Integer userId);
    Integer deleteByNotice_IdAndHead_Id(Integer postId, Integer headId);
}
