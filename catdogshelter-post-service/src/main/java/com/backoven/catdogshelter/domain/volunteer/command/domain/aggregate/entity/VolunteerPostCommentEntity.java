package com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity;

import com.backoven.catdogshelter.common.entity.ShelterheadEntity;
import com.backoven.catdogshelter.common.entity.UserEntity;
import com.backoven.catdogshelter.common.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "volunteerPostComment")
public class VolunteerPostCommentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    private String content;

    @Column(name = "created_at", length = 20, nullable = false)
    private String createdAt;

    @Column(name = "updated_at", length = 20)
    private String updatedAt;

    @Column(name = "is_blinded", nullable = false)
    private Boolean blinded = false;

    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", foreignKey = @ForeignKey(name = "fk_vcomment_head"))
    private ShelterheadEntity head;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_vcomment_user"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vcomment_post"))
    private VolunteerPostEntity post;

    public static VolunteerPostCommentEntity create(VolunteerPostEntity post, String content, UserEntity user, ShelterheadEntity head) {
        var e = new VolunteerPostCommentEntity();
        e.setPost(post);
        e.setContent(content);
        e.setUser(user);
        e.setHead(head);
        e.setCreatedAt(DateTimeUtil.now());
        e.setDeleted(false);
        e.setBlinded(false);
        return e;
    }

    public void edit(String content) {
        this.content = content;
        this.updatedAt = DateTimeUtil.now();
    }

    public void softDelete() {
        this.deleted = true;
        this.updatedAt = DateTimeUtil.now();
    }
}
