package com.backoven.catdogshelter.domain.volunteer.command.domain.repository;

import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.VolunteerPostLikedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerPostLikedRepository extends JpaRepository<VolunteerPostLikedEntity, Integer> {
    boolean existsByPost_IdAndUser_UserId(Integer postId, Integer userId);
    boolean existsByPost_IdAndHead_Id(Integer postId, Integer headId);

    Integer deleteByPost_IdAndUser_UserId(Integer postId, Integer userId);
    Integer deleteByPost_IdAndHead_Id(Integer postId, Integer headId);

    int countByPost_Id(Integer postId);
}
