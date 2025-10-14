package com.backoven.catdogshelter.domain.volunteer.command.domain.aggregate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "volunteerpostfiles")
public class VolunteerPostFileEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_rename", length = 100, nullable = false)
    private String fileRename;

    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath;

    @Column(name = "uploaded_at", length = 20, nullable = false)
    private String uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vfiles_post"))
    private VolunteerPostEntity post;
}
