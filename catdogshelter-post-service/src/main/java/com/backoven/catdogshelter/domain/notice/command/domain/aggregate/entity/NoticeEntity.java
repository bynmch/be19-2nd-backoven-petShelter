package com.backoven.catdogshelter.domain.notice.command.domain.aggregate.entity;

import com.backoven.catdogshelter.common.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "notice")
@ToString
public class NoticeEntity {
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

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "rating_id", nullable = false)
    private Integer ratingId = -1;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<NoticeFileEntity> files = new ArrayList<>();

    public static NoticeEntity newNotice(String title, String content) {
        NoticeEntity notice = new NoticeEntity();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setCreatedAt(DateTimeUtil.now());
        return notice;
    }

    public void modify(String title, String content) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        this.updatedAt = DateTimeUtil.now();
    }

}
