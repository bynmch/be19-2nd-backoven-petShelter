package com.backoven.catdogshelter.domain.volunteer.command.domain.repository;

import com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity.VolunteerAssociationApplicationDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerAssociationApplicationDetailsRepository
        extends JpaRepository<VolunteerAssociationApplicationDetailsEntity, Integer> {

    @Query("""
           select (count(a) > 0)
           from VolunteerAssociationApplicationDetailsEntity a
           where volunteer(a.volunteer) = :volunteerId and id(a.user) = :userId
           """)
    boolean existsByVolunteerAndUser(@Param("volunteerId") Integer volunteerId,
                                     @Param("userId") Integer userId);

    @Modifying
    @Query("delete from VolunteerAssociationApplicationDetailsEntity v where v.volunteer.volunteerId = :volunteerId and v.user.userId = :userId")
    int deleteByVolunteerAndUser(@Param("volunteerId") Integer volunteerId, @Param("userId") Integer userId);

    @Query("""
           select count(a)
           from VolunteerAssociationApplicationDetailsEntity a
           where volunteer(a.volunteer) = :volunteerId
           """)
    long countByVolunteer(@Param("volunteerId") Integer volunteerId);
}
