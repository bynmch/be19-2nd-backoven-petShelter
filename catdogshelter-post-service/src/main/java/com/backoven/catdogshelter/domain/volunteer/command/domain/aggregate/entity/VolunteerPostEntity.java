package com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity;

import com.backoven.catdogshelter.common.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "volunteerPost")
public class VolunteerPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", length = 20, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 20)
    private String updatedAt;

    @Column(nullable = false)
    private Integer view = 0;

    @Column(name = "is_blinded", nullable = false)
    private Boolean blinded = false;

    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = false;

    // 봉사신청내역 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volappdetail_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vpost_volappdetail"))
    private VolunteerAssociationApplicationDetailsEntity volAppDetail;

    // 단방향로 충분 (양방향 필요없으면 @OneToMany 생략해도 됨)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<VolunteerPostFileEntity> files = new ArrayList<>();

    public static VolunteerPostEntity newPost(String title, String content, VolunteerAssociationApplicationDetailsEntity d) {
        var e = new VolunteerPostEntity();
        e.setTitle(title);
        e.setContent(content);
        e.setVolAppDetail(d);
        e.setCreatedAt(DateTimeUtil.now());
        e.setDeleted(false);
        e.setBlinded(false);
        e.setView(0);
        return e;
    }

    public void modify(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        this.updatedAt = DateTimeUtil.now();
    }
}
