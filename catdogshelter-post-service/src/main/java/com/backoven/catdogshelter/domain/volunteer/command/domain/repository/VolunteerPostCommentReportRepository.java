package com.backoven.catdogshelter.domain.volunteer.command.domain.repository;

import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.VolunteerPostCommentReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerPostCommentReportRepository extends JpaRepository<VolunteerPostCommentReportEntity, Integer> {
}
