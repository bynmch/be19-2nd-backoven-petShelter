package com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity;

import com.backoven.catdogshelter.common.entity.ShelterheadEntity;
import com.backoven.catdogshelter.common.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(
        name = "volunteerPostLiked",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_vpl_user", columnNames = {"post_id", "user_id"}),
                @UniqueConstraint(name = "uq_vpl_head", columnNames = {"post_id", "head_id"})
        })
public class VolunteerPostLikedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK: post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vpl_post"))
    private VolunteerPostEntity post;

    // FK: nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", foreignKey = @ForeignKey(name = "fk_vpl_head"))
    private ShelterheadEntity head;

    // FK: nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_vpl_user"))
    private UserEntity user;
}
