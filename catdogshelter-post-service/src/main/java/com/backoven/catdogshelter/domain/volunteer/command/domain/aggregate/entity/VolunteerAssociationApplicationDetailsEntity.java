package com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity;

import com.backoven.catdogshelter.common.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "volunteerAssociationApplicationDetails")
public class VolunteerAssociationApplicationDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 승인 여부(false 대기, true 승인) */
    @Column(nullable = false)
    private Boolean status = false;

    /** 인정 시간(분) – 승인 시 업데이트 가능, 기본 0 */
    @Column
    private Integer time = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vaad_volunteer"))
    private VolunteerAssociationEntity volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vaad_user"))
    private UserEntity user;
}
