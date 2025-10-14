package com.backoven.catdogshelter.domain.volunteer.command.domain.repository;

import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.VolunteerPostFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerPostFileRepository extends JpaRepository<VolunteerPostFileEntity, Integer> {

    // 삭제할 파일들을 번호로 찾음
    List<VolunteerPostFileEntity> findByIdIn(List<Integer> ids);

    // 삭제할 파일들을 번호로 찾음
    List<VolunteerPostFileEntity> findByPostId(Integer postId);
}
