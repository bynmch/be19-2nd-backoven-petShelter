package com.backoven.catdogshelter.domain.notice.command.domain.aggregate.entity;

import com.backoven.catdogshelter.common.entity.ShelterheadEntity;
import com.backoven.catdogshelter.common.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(
        name = "noticeliked",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_nl_user", columnNames = {"notice_id", "user_id"}),
                @UniqueConstraint(name = "uq_nl_head", columnNames = {"notice_id", "head_id"})
        })
public class NoticeLikedEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK: post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false, foreignKey = @ForeignKey(name = "fk_nl_post"))
    private NoticeEntity notice;

    // FK: nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", foreignKey = @ForeignKey(name = "fk_nl_head"))
    private ShelterheadEntity head;

    // FK: nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_nl_user"))
    private UserEntity user;
}
