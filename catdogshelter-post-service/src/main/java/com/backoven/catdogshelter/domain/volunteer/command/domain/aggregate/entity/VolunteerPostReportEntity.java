package com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity;

import com.backoven.catdogshelter.common.entity.ShelterHeadEntity;
import com.backoven.catdogshelter.common.entity.UserEntity;
import com.backoven.catdogshelter.common.util.DateTimeUtil;
import com.backoven.catdogshelter.common.util.ReportCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(
        name = "volunteerPostReport",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_volunteer_post_report_user", columnNames = {"post_id", "user_id"}),
                @UniqueConstraint(name = "uq_volunteer_post_report_head", columnNames = {"post_id", "head_id"})
        })
public class VolunteerPostReportEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReportCategory category;

    @Lob
    @Column(name = "etc_detail")
    private String etcDetail;

    @Column(name = "created_at", length = 20, nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private Boolean status = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vreport_post"))
    private VolunteerPostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_vreport_user"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", foreignKey = @ForeignKey(name = "fk_vreport_head"))
    private ShelterHeadEntity head;

    public static VolunteerPostReportEntity create(VolunteerPostEntity post, ReportCategory category, String etcDetail, UserEntity user, ShelterHeadEntity head) {
        var e = new VolunteerPostReportEntity();
        e.setPost(post);
        e.setCategory(category);
        e.setEtcDetail(etcDetail);
        e.setCreatedAt(DateTimeUtil.now());
        e.setStatus(false);
        e.setUser(user);
        e.setHead(head);
        return e;
    }
}
