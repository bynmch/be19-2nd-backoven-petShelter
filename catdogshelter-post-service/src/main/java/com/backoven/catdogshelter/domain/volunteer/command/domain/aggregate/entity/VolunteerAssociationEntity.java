// VolunteerAssociationEntity.java
package com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity;

import com.backoven.catdogshelter.common.entity.ShelterHeadEntity;
import com.backoven.catdogshelter.common.entity.SigunguEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "volunteerAssociation")
public class VolunteerAssociationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String title;

    @Lob
    private String content;

    @Column(name = "created_at", length = 20, nullable = false)
    private String createdAt;

    /** 봉사 소요 시간(분) 같은 정수 */
    @Column(nullable = false)
    private Integer time;

    /** yyyy-MM-dd HH:mm 같은 문자열로 저장한다고 가정 */
    @Column(name = "start_date", length = 20, nullable = false)
    private String startDate;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(nullable = false)
    private Boolean deadline = false;

    @Column(name = "number_of_people")
    private Integer numberOfPeople;

    @Column(name = "is_end", nullable = false)
    private Boolean isEnd = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", nullable = false, foreignKey = @ForeignKey(name = "fk_va_head"))
    private ShelterHeadEntity head;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_id", nullable = false, foreignKey = @ForeignKey(name = "fk_va_sigungu"))
    private SigunguEntity sigungu;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VolunteerAssociationApplicationDetailsEntity> applications = new ArrayList<>();
}
