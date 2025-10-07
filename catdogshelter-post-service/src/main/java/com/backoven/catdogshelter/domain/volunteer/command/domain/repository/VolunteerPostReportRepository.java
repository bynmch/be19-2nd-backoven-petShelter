package com.backoven.catdogshelter.domain.volunteer.command.domain.repository;

import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.VolunteerPostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerPostReportRepository extends JpaRepository<VolunteerPostReportEntity, Integer> {
}
