package org.minilms.minilms.Entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress",
        uniqueConstraints = @UniqueConstraint(name="uq_progress_member_course", columnNames = {"member_pk","course_id"}))
@Getter @Setter
public class Progress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="member_pk", nullable=false,
            foreignKey=@ForeignKey(name="fk_progress_member_pk"))
    private Member member;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="course_id", nullable=false,
            foreignKey=@ForeignKey(name="fk_progress_course"))
    private Course course;

    @Column(name="progress_percent", nullable=false)
    private double percent; // 0~100

    @Column(nullable=false)
    private LocalDateTime updatedAt;
}
