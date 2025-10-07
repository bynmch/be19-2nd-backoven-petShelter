package com.backoven.catdogshelter.domain.volunteer.command.domain.repository;

import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.VolunteerPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VolunteerPostRepository extends JpaRepository<VolunteerPostEntity, Integer> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update VolunteerPostEntity p set p.deleted = true, p.updatedAt = :now where p.id = :id and p.deleted = false")
    int softDelete(@Param("id") Integer id, @Param("now") String now);
}
